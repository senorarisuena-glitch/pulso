package com.yoshi.pulso

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import java.util.Locale

class PulsoService : Service(), TextToSpeech.OnInitListener {

    companion object {
        const val ACTION_EMPEZAR = "com.yoshi.pulso.EMPEZAR"
        const val ACTION_PAUSA = "com.yoshi.pulso.PAUSA"
        const val ACTION_SALTAR = "com.yoshi.pulso.SALTAR"
        const val ACTION_REP = "com.yoshi.pulso.REP"
        const val ACTION_DESCARTAR = "com.yoshi.pulso.DESCARTAR"
        const val ACTION_TERMINAR = "com.yoshi.pulso.TERMINAR"

        const val CH_ESTADO = "pulso_estado"
        const val CH_AVISO = "pulso_aviso"
        const val NOTIF_ESTADO = 1
        const val NOTIF_AVISO = 2

        @Volatile var activo = false
        @Volatile var enPausa = false
        @Volatile var resumen = "Detenido"
    }

    private val reloj = Handler(Looper.getMainLooper())
    private var tts: TextToSpeech? = null
    private var ttsListo = false
    private var reproductor: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private var fase = "enfoque"
    private var inicioFase = 0L
    private var duracionFase = 0
    private var ciclo = 1
    private var idxFrase = 0
    private var ultimoSeg = -1
    private var idxEj = 0
    private var repsEj = 0
    private val marcasDichas = HashSet<Int>()
    private var descartado = false
    private var ultimoEstadoSeg = -1
    private var restaAlPausar = 0.0

    private lateinit var nm: NotificationManager

    // ---------------------------------------------------------------- ciclo de vida

    override fun onCreate() {
        super.onCreate()
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        crearCanales()
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val t = tts ?: return
            val res = t.setLanguage(Locale("es", "MX"))
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                t.setLanguage(Locale("es", "ES"))
            }
            // guía de navegación: se oye aunque haya música o una llamada en curso
            t.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            ttsListo = true
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        arrancarEnPrimerPlano()

        when (intent?.action) {
            ACTION_EMPEZAR -> if (!activo) empezar()
            ACTION_PAUSA -> alternarPausa()
            ACTION_SALTAR -> saltar()
            ACTION_REP -> sumarRepeticion()
            ACTION_DESCARTAR -> descartarAviso()
            ACTION_TERMINAR -> {
                terminar()
                return START_NOT_STICKY
            }
            else -> if (!activo) empezar()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        activo = false
        reloj.removeCallbacksAndMessages(null)
        callar()
        tts?.shutdown()
        soltarWakeLock()
        super.onDestroy()
    }

    // ---------------------------------------------------------------- motor

    private fun empezar() {
        activo = true
        enPausa = false
        ciclo = 1
        idxFrase = 0
        pedirWakeLock()
        hablar("Empezamos.")
        iniciarFase("enfoque")
        reloj.post(latido)
    }

    private val latido = object : Runnable {
        override fun run() {
            if (activo && !enPausa) tick()
            if (activo) reloj.postDelayed(this, 250)
        }
    }

    private fun iniciarFase(nueva: String) {
        fase = nueva
        inicioFase = SystemClock.elapsedRealtime()
        ultimoSeg = -1
        marcasDichas.clear()
        descartado = false
        if (fase == "enfoque") {
            duracionFase = Config.minEnfoque(this) * 60
            nm.cancel(NOTIF_AVISO)
        } else {
            val b = bloqueDelCiclo()
            duracionFase = b.ejs.sumOf { it.seg }
            idxEj = 0
            repsEj = 0
            vibrar(longArrayOf(0, 120, 80, 120))
            hablar("Arranca. " + b.ejs[0].nombre)
            avisoEjercicio(b.ejs[0].nombre, if (b.ejs.size > 1) b.ejs[1].nombre else null, duracionFase, true)
        }
        actualizarEstado()
    }

    private fun tick() {
        val t = (SystemClock.elapsedRealtime() - inicioFase) / 1000.0
        val resta = (duracionFase - t).coerceAtLeast(0.0)
        val seg = Math.ceil(resta).toInt()

        if (fase == "enfoque") {
            val paso = Config.avisoFrases(this) * 60
            val marca = (t / paso).toInt()
            if (marca >= 1 && t < duracionFase - 60 && !marcasDichas.contains(marca)) {
                marcasDichas.add(marca)
                lanzarFrase()
            }

            if (resta <= 60) {
                if (seg != ultimoSeg) {
                    ultimoSeg = seg
                    if (seg == 60) {
                        descartado = false
                        callar()
                        hablar("Un minuto. Prepárate.")
                        vibrar(longArrayOf(0, 200))
                    } else if (Config.cuentaSegundo(this) && seg in 1..59) {
                        hablar(seg.toString())
                    } else if (!Config.cuentaSegundo(this) && (seg == 30 || seg == 10 || seg <= 5)) {
                        hablar(seg.toString())
                    }
                    avisoCuenta(seg, seg == 60)
                }
            }

            if (resta <= 0.0) {
                iniciarFase("mover")
                return
            }
        } else {
            val ejs = bloqueDelCiclo().ejs
            var acc = 0
            var i = ejs.size - 1
            for (k in ejs.indices) {
                acc += ejs[k].seg
                if (t < acc) { i = k; break }
            }
            if (i != idxEj) {
                idxEj = i
                repsEj = 0
                descartado = false
                callar()
                hablar("Cambio. " + ejs[i].nombre)
                vibrar(longArrayOf(0, 90))
                avisoEjercicio(ejs[i].nombre, if (i + 1 < ejs.size) ejs[i + 1].nombre else null, seg, true)
            }
            if (seg != ultimoSeg) {
                ultimoSeg = seg
                if (Config.avisoDiez(this) && seg % 10 == 0 && seg > 0 && seg < duracionFase) {
                    hablar(tiempoEnPalabras(seg))
                }
                avisoEjercicio(ejs[i].nombre, if (i + 1 < ejs.size) ejs[i + 1].nombre else null, seg, false)
            }

            if (resta <= 0.0) {
                ciclo++
                callar()
                hablar("Muy bien. Vuelve al trabajo.")
                iniciarFase("enfoque")
                return
            }
        }
        if (seg != ultimoEstadoSeg) { ultimoEstadoSeg = seg; actualizarEstado() }
    }

    private fun bloqueDelCiclo(): Bloque {
        val bs = Config.bloques(this)
        return bs[(ciclo - 1) % bs.size]
    }

    private fun alternarPausa() {
        if (!activo) return
        if (!enPausa) {
            restaAlPausar = duracionFase - (SystemClock.elapsedRealtime() - inicioFase) / 1000.0
            enPausa = true
            callar()
            soltarWakeLock()
        } else {
            enPausa = false
            inicioFase = SystemClock.elapsedRealtime() - ((duracionFase - restaAlPausar) * 1000).toLong()
            pedirWakeLock()
        }
        actualizarEstado()
    }

    private fun saltar() {
        if (!activo) return
        callar()
        if (fase == "enfoque") iniciarFase("mover")
        else { ciclo++; iniciarFase("enfoque") }
    }

    private fun sumarRepeticion() {
        if (fase != "mover") return
        val ejs = bloqueDelCiclo().ejs
        val nombre = ejs[idxEj.coerceIn(0, ejs.size - 1)].nombre
        repsEj++
        Config.sumarRep(this, nombre, 1)
        vibrar(longArrayOf(0, 25))
        val t = (SystemClock.elapsedRealtime() - inicioFase) / 1000.0
        val seg = Math.ceil((duracionFase - t).coerceAtLeast(0.0)).toInt()
        descartado = false
        avisoEjercicio(nombre, if (idxEj + 1 < ejs.size) ejs[idxEj + 1].nombre else null, seg, false)
        actualizarEstado()
    }

    private fun descartarAviso() {
        callar()
        descartado = true
        nm.cancel(NOTIF_AVISO)
    }

    private fun terminar() {
        activo = false
        enPausa = false
        reloj.removeCallbacksAndMessages(null)
        callar()
        nm.cancel(NOTIF_AVISO)
        soltarWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // ---------------------------------------------------------------- voz

    private fun lanzarFrase() {
        val frases = Config.frases(this).filter { it.texto.isNotBlank() || it.audio != null }
        if (frases.isEmpty()) return
        val f = frases[idxFrase % frases.size]
        idxFrase++
        descartado = false
        avisoFrase(f.texto.ifBlank { "Audio" })
        vibrar(longArrayOf(0, 60))
        if (f.audio != null) {
            if (reproducirAudio(f.audio)) return
        }
        if (f.texto.isNotBlank()) hablar(f.texto)
    }

    private fun reproducirAudio(uri: String): Boolean {
        return try {
            callar()
            val mp = MediaPlayer()
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            mp.setDataSource(this, Uri.parse(uri))
            mp.setOnCompletionListener {
                it.release()
                if (reproductor === it) reproductor = null
            }
            mp.prepare()
            mp.start()
            reproductor = mp
            true
        } catch (e: Exception) {
            reproductor = null
            false
        }
    }

    private fun hablar(texto: String) {
        val t = tts ?: return
        if (!ttsListo) return
        val params = Bundle()
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        t.speak(digitosSueltos(texto), TextToSpeech.QUEUE_ADD, params, "pulso")
    }

    private fun callar() {
        try { tts?.stop() } catch (e: Exception) { }
        try { reproductor?.stop(); reproductor?.release() } catch (e: Exception) { }
        reproductor = null
    }

    private fun vibrar(patron: LongArray) {
        if (!Config.vibrar(this)) return
        try {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(VibrationEffect.createWaveform(patron, -1))
        } catch (e: Exception) { }
    }

    // ---------------------------------------------------------------- números en palabras

    private val digitos = arrayOf("cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve")
    private val nums = arrayOf(
        "cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve",
        "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete",
        "dieciocho", "diecinueve", "veinte", "veintiuno", "veintidós", "veintitrés",
        "veinticuatro", "veinticinco", "veintiséis", "veintisiete", "veintiocho", "veintinueve"
    )

    private fun numEnPalabras(n: Int): String {
        if (n < 30) return nums[n]
        val d = when (n / 10) { 3 -> "treinta"; 4 -> "cuarenta"; 5 -> "cincuenta"; else -> "sesenta" }
        val u = n % 10
        return if (u == 0) d else "$d y ${nums[u]}"
    }

    private fun tiempoEnPalabras(s: Int): String {
        if (s < 60) return numEnPalabras(s) + " segundos"
        val m = s / 60
        val r = s % 60
        return if (r == 0) numEnPalabras(m) + (if (m == 1) " minuto" else " minutos")
        else numEnPalabras(m) + " " + numEnPalabras(r)
    }

    /** Los códigos como 1,1,9,8,1 se leen dígito por dígito, no como un número entero. */
    private fun digitosSueltos(t: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < t.length) {
            val c = t[i]
            if (c.isDigit()) {
                val ini = i
                while (i < t.length && t[i].isDigit()) i++
                val bloque = t.substring(ini, i)
                sb.append(bloque.map { digitos[it - '0'] }.joinToString(", "))
            } else {
                sb.append(c)
                i++
            }
        }
        return sb.toString()
    }

    // ---------------------------------------------------------------- notificaciones

    private fun crearCanales() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val estado = NotificationChannel(CH_ESTADO, "Estado del ciclo", NotificationManager.IMPORTANCE_LOW)
            estado.setShowBadge(false)
            estado.description = "Aviso permanente mientras Pulso corre"

            val aviso = NotificationChannel(CH_AVISO, "Frases y ejercicios", NotificationManager.IMPORTANCE_HIGH)
            aviso.description = "Frases, cuenta regresiva y ejercicios"
            aviso.enableVibration(false)
            aviso.setSound(null, null)

            nm.createNotificationChannel(estado)
            nm.createNotificationChannel(aviso)
        }
    }

    private fun piServicio(accion: String, codigo: Int): PendingIntent {
        val i = Intent(this, PulsoService::class.java).setAction(accion)
        return PendingIntent.getService(
            this, codigo, i,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun piApp(): PendingIntent {
        val i = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(this, 100, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun arrancarEnPrimerPlano() {
        val n = notifEstado()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIF_ESTADO, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(NOTIF_ESTADO, n)
            }
        } catch (e: Exception) {
            startForeground(NOTIF_ESTADO, n)
        }
    }

    private fun notifEstado(): Notification {
        val titulo: String
        val texto: String
        if (!activo) {
            titulo = "Pulso detenido"
            texto = "Toca para abrir"
        } else if (enPausa) {
            titulo = "En pausa"
            texto = "Ciclo $ciclo"
        } else {
            val t = (SystemClock.elapsedRealtime() - inicioFase) / 1000.0
            val seg = Math.ceil((duracionFase - t).coerceAtLeast(0.0)).toInt()
            titulo = if (fase == "enfoque") {
                if (seg <= 60) "Prepárate · $seg" else "Enfoque · " + mmss(seg)
            } else {
                val ejs = bloqueDelCiclo().ejs
                ejs[idxEj.coerceIn(0, ejs.size - 1)].nombre + " · " + mmss(seg)
            }
            texto = "Ciclo $ciclo · " + bloqueDelCiclo().nombre + " · " + Config.totalHoy(this) + " reps hoy"
        }
        resumen = titulo

        return NotificationCompat.Builder(this, CH_ESTADO)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setContentIntent(piApp())
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(0, if (enPausa) "Continuar" else "Pausar", piServicio(ACTION_PAUSA, 11))
            .addAction(0, "Saltar", piServicio(ACTION_SALTAR, 12))
            .addAction(0, "Terminar", piServicio(ACTION_TERMINAR, 13))
            .build()
    }

    private fun actualizarEstado() {
        try { nm.notify(NOTIF_ESTADO, notifEstado()) } catch (e: Exception) { }
    }

    private fun baseAviso(alertar: Boolean): NotificationCompat.Builder =
        NotificationCompat.Builder(this, CH_AVISO)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentIntent(piApp())
            .setDeleteIntent(piServicio(ACTION_DESCARTAR, 20))
            .setAutoCancel(false)
            .setOnlyAlertOnce(!alertar)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)

    private fun avisoFrase(texto: String) {
        if (descartado) return
        val n = baseAviso(true)
            .setContentTitle("Recuerda")
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .addAction(0, "Callar", piServicio(ACTION_DESCARTAR, 21))
            .build()
        try { nm.notify(NOTIF_AVISO, n) } catch (e: Exception) { }
    }

    private fun avisoCuenta(seg: Int, alertar: Boolean) {
        if (descartado) return
        val n = baseAviso(alertar)
            .setContentTitle(seg.toString())
            .setContentText("Prepárate · " + bloqueDelCiclo().nombre)
            .setUsesChronometer(false)
            .addAction(0, "Callar", piServicio(ACTION_DESCARTAR, 22))
            .build()
        try { nm.notify(NOTIF_AVISO, n) } catch (e: Exception) { }
    }

    private fun avisoEjercicio(nombre: String, siguiente: String?, seg: Int, alertar: Boolean) {
        if (descartado) return
        val detalle = mmss(seg) + " · " + repsEj + " reps" +
                (if (siguiente != null) " · sigue: $siguiente" else " · último")
        val n = baseAviso(alertar)
            .setContentTitle(nombre)
            .setContentText(detalle)
            .addAction(0, "+1 rep", piServicio(ACTION_REP, 23))
            .addAction(0, "Saltar", piServicio(ACTION_SALTAR, 24))
            .addAction(0, "Callar", piServicio(ACTION_DESCARTAR, 25))
            .build()
        try { nm.notify(NOTIF_AVISO, n) } catch (e: Exception) { }
    }

    private fun mmss(s: Int): String = (s / 60).toString() + ":" + String.format(Locale.US, "%02d", s % 60)

    // ---------------------------------------------------------------- energía

    private fun pedirWakeLock() {
        if (wakeLock != null) return
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "pulso:ciclo")
            wl.setReferenceCounted(false)
            wl.acquire()
            wakeLock = wl
        } catch (e: Exception) { }
    }

    private fun soltarWakeLock() {
        try { if (wakeLock?.isHeld == true) wakeLock?.release() } catch (e: Exception) { }
        wakeLock = null
    }
}
