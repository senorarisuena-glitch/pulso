package com.yoshi.pulso

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Ejercicio(val nombre: String, val seg: Int)
data class Bloque(val nombre: String, val ejs: List<Ejercicio>)
data class Frase(val id: String, val texto: String, val audio: String?)

object Config {

    private const val PREFS = "pulso"

    fun p(c: Context): SharedPreferences = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ---------- números ----------
    fun minEnfoque(c: Context) = p(c).getInt("minEnfoque", 25)
    fun avisoFrases(c: Context) = p(c).getInt("avisoFrases", 4)
    fun cuentaSegundo(c: Context) = p(c).getBoolean("cuentaSegundo", true)
    fun avisoDiez(c: Context) = p(c).getBoolean("avisoDiez", true)
    fun vibrar(c: Context) = p(c).getBoolean("vibrar", true)

    fun guardarNumeros(c: Context, min: Int, aviso: Int, seg: Boolean, diez: Boolean, vib: Boolean) {
        p(c).edit()
            .putInt("minEnfoque", min)
            .putInt("avisoFrases", aviso)
            .putBoolean("cuentaSegundo", seg)
            .putBoolean("avisoDiez", diez)
            .putBoolean("vibrar", vib)
            .apply()
    }

    // ---------- bloques ----------
    private fun bloquesPorDefecto(): List<Bloque> = listOf(
        Bloque(
            "Bloque 1", listOf(
                Ejercicio("Sentadillas", 100),
                Ejercicio("Lagartijas", 100),
                Ejercicio("Plancha y estiramiento de cadera", 100)
            )
        ),
        Bloque(
            "Bloque 2", listOf(
                Ejercicio("Burpees", 100),
                Ejercicio("Brincos altos", 100),
                Ejercicio("Posición de caballo", 100)
            )
        ),
        Bloque(
            "Bloque 3", listOf(
                Ejercicio("Lagartija parado de manos", 100),
                Ejercicio("Saltos de rana", 100),
                Ejercicio("Abdominales con los pies", 100)
            )
        )
    )

    fun bloques(c: Context): List<Bloque> {
        val txt = p(c).getString("bloques", null) ?: return bloquesPorDefecto()
        return try {
            val arr = JSONArray(txt)
            val out = ArrayList<Bloque>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val ejsArr = o.getJSONArray("ejs")
                val ejs = ArrayList<Ejercicio>()
                for (j in 0 until ejsArr.length()) {
                    val e = ejsArr.getJSONObject(j)
                    ejs.add(Ejercicio(e.getString("n"), e.getInt("s")))
                }
                if (ejs.isNotEmpty()) out.add(Bloque(o.getString("nombre"), ejs))
            }
            if (out.isEmpty()) bloquesPorDefecto() else out
        } catch (e: Exception) {
            bloquesPorDefecto()
        }
    }

    fun guardarBloques(c: Context, lista: List<Bloque>) {
        val arr = JSONArray()
        for (b in lista) {
            val o = JSONObject()
            o.put("nombre", b.nombre)
            val ejs = JSONArray()
            for (e in b.ejs) {
                val eo = JSONObject()
                eo.put("n", e.nombre)
                eo.put("s", e.seg)
                ejs.put(eo)
            }
            o.put("ejs", ejs)
            arr.put(o)
        }
        p(c).edit().putString("bloques", arr.toString()).apply()
    }

    fun textoDeBloque(b: Bloque): String =
        b.ejs.joinToString("\n") { it.nombre + " | " + it.seg }

    fun bloqueDeTexto(nombre: String, texto: String): Bloque {
        val ejs = texto.split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { linea ->
                val partes = linea.split("|")
                val n = partes[0].trim().ifEmpty { "Movimiento" }
                val s = if (partes.size > 1) partes[1].trim().toIntOrNull() ?: 100 else 100
                Ejercicio(n, if (s > 0) s else 100)
            }
        return Bloque(nombre, if (ejs.isEmpty()) listOf(Ejercicio("Movimiento", 100)) else ejs)
    }

    // ---------- frases ----------
    private fun frasesPorDefecto(): List<Frase> = listOf(
        "Respira, concéntrate, recuerda quién eres y a dónde vas. Eres un victor, la victoria está en ti. Eres un vencedor.",
        "Agradece, conecta tu ser superior. 1, 1, 9, 8, 1. Dios habita en ti.",
        "Yo soy la fuente en completa expresión, lo integro, lo comparto, libero todo límite, manifiesto la realidad perfecta aquí y ahora.",
        "Tu percepción cambia tu realidad. Cambia cómo estás percibiendo las cosas a positivo. Tu mente es un proyector. Tu desafío es ser millonario, estar presente y disfrutar de una hermosa familia. Los problemas y obstáculos son una oportunidad.",
        "Yo soy las riquezas de Dios fluyendo a mis manos y uso, que nada ni nadie puede detener.",
        "Estás creando una nueva identidad. Obsérvala, obsérvala. Agradécele, agradécele.",
        "Ángeles y guías, les doy permiso de asistirme en todas las áreas de mi vida. Límpienme y protéjanme de toda negatividad.",
        "5, 2, 0. 7, 4, 1, 8."
    ).mapIndexed { i, t -> Frase("f" + (i + 1), t, null) }

    fun frases(c: Context): List<Frase> {
        val txt = p(c).getString("frases", null) ?: return frasesPorDefecto()
        return try {
            val arr = JSONArray(txt)
            val out = ArrayList<Frase>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val audio = if (o.isNull("audio")) null else o.getString("audio")
                out.add(Frase(o.getString("id"), o.getString("texto"), audio))
            }
            out
        } catch (e: Exception) {
            frasesPorDefecto()
        }
    }

    fun guardarFrases(c: Context, lista: List<Frase>) {
        val arr = JSONArray()
        for (f in lista) {
            val o = JSONObject()
            o.put("id", f.id)
            o.put("texto", f.texto)
            if (f.audio == null) o.put("audio", JSONObject.NULL) else o.put("audio", f.audio)
            arr.put(o)
        }
        p(c).edit().putString("frases", arr.toString()).apply()
    }

    // ---------- repeticiones ----------
    private fun hoy(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun sumarRep(c: Context, ejercicio: String, n: Int) {
        val clave = "reps_" + hoy()
        val actual = try {
            JSONObject(p(c).getString(clave, "{}") ?: "{}")
        } catch (e: Exception) {
            JSONObject()
        }
        val nuevo = (actual.optInt(ejercicio, 0) + n).coerceAtLeast(0)
        actual.put(ejercicio, nuevo)
        p(c).edit().putString(clave, actual.toString()).apply()
    }

    fun totalHoy(c: Context): Int {
        val o = try {
            JSONObject(p(c).getString("reps_" + hoy(), "{}") ?: "{}")
        } catch (e: Exception) {
            JSONObject()
        }
        var total = 0
        val it = o.keys()
        while (it.hasNext()) total += o.optInt(it.next(), 0)
        return total
    }

    fun desgloseHoy(c: Context): String {
        val o = try {
            JSONObject(p(c).getString("reps_" + hoy(), "{}") ?: "{}")
        } catch (e: Exception) {
            JSONObject()
        }
        val filas = ArrayList<String>()
        val it = o.keys()
        while (it.hasNext()) {
            val k = it.next()
            val v = o.optInt(k, 0)
            if (v > 0) filas.add("$k — $v")
        }
        return if (filas.isEmpty()) "Todavía nada hoy."
        else filas.joinToString("\n") + "\n\nTotal — " + totalHoy(c)
    }

    fun borrarHoy(c: Context) {
        p(c).edit().remove("reps_" + hoy()).apply()
    }
}
