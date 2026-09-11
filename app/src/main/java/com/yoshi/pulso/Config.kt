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
data class Perfil(val id: String, val nombre: String)
data class ListaGuardada(val nombre: String, val frases: List<Frase>)

object Config {

    private const val PREFS = "pulso"

    /** El ciclo es fijo: 25 minutos de enfoque, 5 de movimiento. */
    const val MINUTOS_ENFOQUE = 25

    fun p(c: Context): SharedPreferences = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ================================================================ perfiles

    fun perfiles(c: Context): List<Perfil> {
        val txt = p(c).getString("perfiles", null)
        if (txt == null) {
            migrarDesdeVersionVieja(c)
            return listOf(Perfil("u1", "Yo"))
        }
        return try {
            val arr = JSONArray(txt)
            val out = ArrayList<Perfil>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(Perfil(o.getString("id"), o.getString("nombre")))
            }
            if (out.isEmpty()) listOf(Perfil("u1", "Yo")) else out
        } catch (e: Exception) {
            listOf(Perfil("u1", "Yo"))
        }
    }

    private fun guardarPerfiles(c: Context, lista: List<Perfil>) {
        val arr = JSONArray()
        for (u in lista) {
            val o = JSONObject()
            o.put("id", u.id)
            o.put("nombre", u.nombre)
            arr.put(o)
        }
        p(c).edit().putString("perfiles", arr.toString()).apply()
    }

    /** La primera vez, lo que ya estaba guardado se vuelve el perfil "Yo". */
    private fun migrarDesdeVersionVieja(c: Context) {
        val pr = p(c)
        val ed = pr.edit()
        ed.putString("perfiles", "[{\"id\":\"u1\",\"nombre\":\"Yo\"}]")
        ed.putString("perfilActual", "u1")
        val bloquesViejos = pr.getString("bloques", null)
        if (bloquesViejos != null) ed.putString("u1__bloques", bloquesViejos)
        val frasesViejas = pr.getString("frases", null)
        if (frasesViejas != null) ed.putString("u1__frases", frasesViejas)
        ed.putInt("u1__avisoFrases", pr.getInt("avisoFrases", 4))
        ed.putBoolean("u1__cuentaSegundo", pr.getBoolean("cuentaSegundo", true))
        ed.putBoolean("u1__avisoDiez", pr.getBoolean("avisoDiez", true))
        ed.putBoolean("u1__vibrar", pr.getBoolean("vibrar", true))
        ed.apply()
    }

    fun perfilActual(c: Context): String {
        val id = p(c).getString("perfilActual", null)
        val lista = perfiles(c)
        if (id != null && lista.any { it.id == id }) return id
        val primero = lista[0].id
        p(c).edit().putString("perfilActual", primero).apply()
        return primero
    }

    fun nombrePerfilActual(c: Context): String {
        val id = perfilActual(c)
        return perfiles(c).firstOrNull { it.id == id }?.nombre ?: "Yo"
    }

    fun ponerPerfilActual(c: Context, id: String) {
        p(c).edit().putString("perfilActual", id).apply()
    }

    fun crearPerfil(c: Context, nombre: String): String {
        val id = "u" + System.currentTimeMillis()
        val lista = ArrayList(perfiles(c))
        lista.add(Perfil(id, if (nombre.isBlank()) "Nuevo" else nombre))
        guardarPerfiles(c, lista)
        ponerPerfilActual(c, id)
        return id
    }

    fun renombrarPerfil(c: Context, id: String, nombre: String) {
        if (nombre.isBlank()) return
        val lista = perfiles(c).map { if (it.id == id) Perfil(id, nombre) else it }
        guardarPerfiles(c, lista)
    }

    /** Borra el perfil y todo lo que guardó. Nunca deja la lista vacía. */
    fun borrarPerfil(c: Context, id: String): Boolean {
        val lista = perfiles(c)
        if (lista.size <= 1) return false
        val ed = p(c).edit()
        for (clave in listOf("bloques", "frases", "avisoFrases", "cuentaSegundo", "avisoDiez", "vibrar")) {
            ed.remove(id + "__" + clave)
        }
        ed.apply()
        guardarPerfiles(c, lista.filter { it.id != id })
        ponerPerfilActual(c, perfiles(c)[0].id)
        return true
    }

    private fun k(c: Context, clave: String) = perfilActual(c) + "__" + clave

    // ================================================================ números

    fun minEnfoque(c: Context) = MINUTOS_ENFOQUE
    fun avisoFrases(c: Context) = p(c).getInt(k(c, "avisoFrases"), 4)
    fun cuentaSegundo(c: Context) = p(c).getBoolean(k(c, "cuentaSegundo"), true)
    fun avisoDiez(c: Context) = p(c).getBoolean(k(c, "avisoDiez"), true)
    fun vibrar(c: Context) = p(c).getBoolean(k(c, "vibrar"), true)

    fun guardarNumeros(c: Context, aviso: Int, seg: Boolean, diez: Boolean, vib: Boolean) {
        p(c).edit()
            .putInt(k(c, "avisoFrases"), if (aviso > 0) aviso else 4)
            .putBoolean(k(c, "cuentaSegundo"), seg)
            .putBoolean(k(c, "avisoDiez"), diez)
            .putBoolean(k(c, "vibrar"), vib)
            .apply()
    }

    // ================================================================ bloques

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
        val txt = p(c).getString(k(c, "bloques"), null) ?: return bloquesPorDefecto()
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
        p(c).edit().putString(k(c, "bloques"), arr.toString()).apply()
    }

    fun textoDeBloque(b: Bloque): String =
        b.ejs.joinToString("\n") { it.nombre + " | " + it.seg }

    fun bloqueDeTexto(nombre: String, texto: String): Bloque {
        val ejs = texto.split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { linea ->
                val partes = linea.split("|")
                val n = if (partes[0].trim().isEmpty()) "Movimiento" else partes[0].trim()
                val s = if (partes.size > 1) partes[1].trim().toIntOrNull() ?: 100 else 100
                Ejercicio(n, if (s > 0) s else 100)
            }
        return Bloque(nombre, if (ejs.isEmpty()) listOf(Ejercicio("Movimiento", 100)) else ejs)
    }

    // ================================================================ frases

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
        val txt = p(c).getString(k(c, "frases"), null) ?: return frasesPorDefecto()
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
        p(c).edit().putString(k(c, "frases"), arr.toString()).apply()
    }

    // ================================================================ mis listas de frases

    /** Las listas que el usuario guarda. Son compartidas entre todos los perfiles. */
    fun misListas(c: Context): List<ListaGuardada> {
        val txt = p(c).getString("misListas", null) ?: return emptyList()
        return try {
            val arr = JSONArray(txt)
            val out = ArrayList<ListaGuardada>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val fr = o.getJSONArray("frases")
                val lista = ArrayList<Frase>()
                for (j in 0 until fr.length()) {
                    val f = fr.getJSONObject(j)
                    val audio = if (f.isNull("audio")) null else f.getString("audio")
                    lista.add(Frase(f.getString("id"), f.getString("texto"), audio))
                }
                out.add(ListaGuardada(o.getString("nombre"), lista))
            }
            out
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun escribirListas(c: Context, listas: List<ListaGuardada>) {
        val arr = JSONArray()
        for (l in listas) {
            val o = JSONObject()
            o.put("nombre", l.nombre)
            val fr = JSONArray()
            for (f in l.frases) {
                val fo = JSONObject()
                fo.put("id", f.id)
                fo.put("texto", f.texto)
                if (f.audio == null) fo.put("audio", JSONObject.NULL) else fo.put("audio", f.audio)
                fr.put(fo)
            }
            o.put("frases", fr)
            arr.put(o)
        }
        p(c).edit().putString("misListas", arr.toString()).apply()
    }

    /** Guarda con ese nombre. Si ya existía una lista igual, la reemplaza. */
    fun guardarLista(c: Context, nombre: String, frases: List<Frase>) {
        if (nombre.isBlank()) return
        val actuales = ArrayList(misListas(c).filter { it.nombre != nombre })
        actuales.add(ListaGuardada(nombre, frases))
        escribirListas(c, actuales)
    }

    fun borrarLista(c: Context, nombre: String) {
        escribirListas(c, misListas(c).filter { it.nombre != nombre })
    }

    // ================================================================ repeticiones

    private fun hoy(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun claveReps(c: Context) = "reps_" + perfilActual(c) + "_" + hoy()

    fun sumarRep(c: Context, ejercicio: String, n: Int) {
        val clave = claveReps(c)
        val actual = try {
            JSONObject(p(c).getString(clave, "{}") ?: "{}")
        } catch (e: Exception) {
            JSONObject()
        }
        val nuevo = actual.optInt(ejercicio, 0) + n
        actual.put(ejercicio, if (nuevo < 0) 0 else nuevo)
        p(c).edit().putString(clave, actual.toString()).apply()
    }

    fun totalHoy(c: Context): Int {
        val o = try {
            JSONObject(p(c).getString(claveReps(c), "{}") ?: "{}")
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
            JSONObject(p(c).getString(claveReps(c), "{}") ?: "{}")
        } catch (e: Exception) {
            JSONObject()
        }
        val filas = ArrayList<String>()
        val it = o.keys()
        while (it.hasNext()) {
            val kk = it.next()
            val v = o.optInt(kk, 0)
            if (v > 0) filas.add("$kk — $v")
        }
        return if (filas.isEmpty()) "Todavía nada hoy."
        else filas.joinToString("\n") + "\n\nTotal — " + totalHoy(c)
    }

    fun borrarHoy(c: Context) {
        p(c).edit().remove(claveReps(c)).apply()
    }
}
