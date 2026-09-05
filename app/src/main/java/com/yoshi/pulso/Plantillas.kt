package com.yoshi.pulso

/**
 * Plantillas listas. Una rutina llena los tres bloques de golpe.
 * Un paquete de frases reemplaza la lista de frases del perfil.
 */
object Plantillas {

    data class Rutina(val nombre: String, val descripcion: String, val bloques: List<Bloque>)
    data class Paquete(val nombre: String, val descripcion: String, val frases: List<String>)

    private fun b(nombre: String, vararg ejs: String): Bloque =
        Bloque(nombre, ejs.map { Ejercicio(it, 100) })

    // ------------------------------------------------------------------ rutinas

    val rutinas: List<Rutina> = listOf(

        Rutina(
            "Bajar de peso",
            "Cardio corto y constante. Sudas y el corazón se acelera.",
            listOf(
                b("Bloque 1", "Brincos de tijera", "Rodillas al pecho corriendo", "Sentadillas rápidas"),
                b("Bloque 2", "Burpees", "Escaladores", "Brincos altos"),
                b("Bloque 3", "Sombra de boxeo", "Desplantes alternados", "Plancha con toque de hombro")
            )
        ),

        Rutina(
            "Ganar músculo",
            "Fuerza con tu propio peso. Movimientos lentos y controlados.",
            listOf(
                b("Bloque 1", "Lagartijas lentas", "Sentadillas profundas", "Plancha firme"),
                b("Bloque 2", "Lagartijas diamante", "Desplantes con pausa", "Puente de glúteo a una pierna"),
                b("Bloque 3", "Lagartija parado de manos", "Sentadilla búlgara en silla", "Fondos de tríceps en silla")
            )
        ),

        Rutina(
            "Pecho y brazos",
            "Empuje. Todo lo que trabaja pecho, hombro y tríceps.",
            listOf(
                b("Bloque 1", "Lagartijas normales", "Lagartijas abiertas", "Plancha alta"),
                b("Bloque 2", "Lagartijas diamante", "Fondos de tríceps en silla", "Lagartijas inclinadas en pared"),
                b("Bloque 3", "Lagartija parado de manos", "Lagartijas con pausa abajo", "Círculos de brazos sostenidos")
            )
        ),

        Rutina(
            "Piernas y glúteos",
            "Tren inferior. Vas a sentirlas al día siguiente.",
            listOf(
                b("Bloque 1", "Sentadillas", "Desplantes alternados", "Puente de glúteo"),
                b("Bloque 2", "Sentadilla sumo", "Sentadilla búlgara en silla", "Elevación de talones"),
                b("Bloque 3", "Saltos de rana", "Sentadilla isométrica en pared", "Patada de glúteo en cuatro puntos")
            )
        ),

        Rutina(
            "Abdomen y core",
            "Centro fuerte. Cuida la espalda baja, no jalones el cuello.",
            listOf(
                b("Bloque 1", "Abdominales con los pies", "Plancha frontal", "Tijeras acostado"),
                b("Bloque 2", "Escaladores", "Plancha lateral alternada", "Elevación de piernas"),
                b("Bloque 3", "Giro ruso sentado", "Hueco abdominal sostenido", "Plancha con toque de hombro")
            )
        ),

        Rutina(
            "Oficina sin sudar",
            "Mueve la sangre sin despeinarte. Sirve con camisa puesta.",
            listOf(
                b("Bloque 1", "Marcha en el lugar", "Rotación de hombros", "Estiramiento de cuello lento"),
                b("Bloque 2", "Sentadillas a la silla sin peso", "Elevación de talones parado", "Apertura de pecho en marco de puerta"),
                b("Bloque 3", "Rotación de cadera de pie", "Estiramiento de muñecas y dedos", "Respiración profunda con brazos arriba")
            )
        ),

        Rutina(
            "Espalda y postura",
            "Para el que pasa el día sentado. Abre lo que la silla cierra.",
            listOf(
                b("Bloque 1", "Gato y vaca en cuatro puntos", "Superman acostado", "Retracción de escápulas"),
                b("Bloque 2", "Estiramiento de psoas en desplante", "Plancha frontal", "Apertura de pecho en pared"),
                b("Bloque 3", "Rotación de columna sentado", "Puente de glúteo", "Colgarse o estirarse hacia arriba")
            )
        ),

        Rutina(
            "Energía rápida",
            "Despierta el cuerpo en segundos. Ideal para la mañana.",
            listOf(
                b("Bloque 1", "Brincos de tijera", "Sacudida de brazos y piernas", "Respiración rápida de pie"),
                b("Bloque 2", "Rodillas al pecho corriendo", "Sentadillas con impulso", "Golpes al aire"),
                b("Bloque 3", "Brincos altos", "Escaladores", "Estiramiento largo hacia el techo")
            )
        ),

        Rutina(
            "Movilidad y flexibilidad",
            "Articulaciones sueltas. Nada de fuerza, todo rango.",
            listOf(
                b("Bloque 1", "Círculos de cadera", "Círculos de hombro", "Rotación de tobillos"),
                b("Bloque 2", "Sentadilla profunda sostenida", "Estiramiento de isquiotibiales", "Postura del niño"),
                b("Bloque 3", "Desplante con rotación", "Puente suave de espalda", "Estiramiento de cuádriceps de pie")
            )
        ),

        Rutina(
            "Resistencia",
            "Aguante. Bloques largos, ritmo constante, sin parar.",
            listOf(
                b("Bloque 1", "Trote en el lugar", "Sentadillas continuas", "Plancha sostenida"),
                b("Bloque 2", "Burpees a ritmo lento", "Escaladores continuos", "Desplantes caminando"),
                b("Bloque 3", "Brincos de tijera", "Sombra de boxeo", "Sentadilla isométrica en pared")
            )
        )
    )

    // ------------------------------------------------------------------ frases

    val paquetes: List<Paquete> = listOf(

        Paquete(
            "Motivación y disciplina",
            "Para los días en que no tienes ganas.",
            listOf(
                "No estás esperando el momento. El momento es este.",
                "La disciplina es acordarte de lo que quieres de verdad.",
                "Nadie viene a rescatarte, y está bien. Puedes tú.",
                "Lo difícil hoy es lo normal en tres meses.",
                "Hazlo cansado. Hazlo con flojera. Nada más hazlo.",
                "Cada vez que cumples contigo, te vuelves más confiable ante ti mismo.",
                "El que sigue cuando ya nadie está viendo, gana."
            )
        ),

        Paquete(
            "Autoestima",
            "Para dejar de hablarte como enemigo.",
            listOf(
                "Mereces el mismo respeto que le das a los demás.",
                "Tu valor no depende de lo que produjiste hoy.",
                "Háblate como le hablarías a alguien que amas.",
                "Estás aprendiendo. Eso no es lo mismo que estar fallando.",
                "Tienes derecho a ocupar espacio y a decir lo que piensas.",
                "Lo que sientes es válido aunque nadie más lo entienda.",
                "No tienes que ser perfecto para ser suficiente."
            )
        ),

        Paquete(
            "Elevar la vibración",
            "Para cambiar el estado interno en el momento.",
            listOf(
                "Respira profundo. Todo se acomoda desde la calma.",
                "Estás en sintonía con lo bueno que ya viene en camino.",
                "Suelta lo pesado. No es tuyo y no lo tienes que cargar.",
                "Tu energía abre puertas antes de que digas una palabra.",
                "Donde pones tu atención, ahí crece la vida.",
                "Agradece antes de tener. Así se llama.",
                "Estás alineado. Estás en paz. Estás en tu lugar."
            )
        ),

        Paquete(
            "Yo soy",
            "Decretos en primera persona, en presente.",
            listOf(
                "Yo soy la fuente en completa expresión, aquí y ahora.",
                "Yo soy salud, fuerza y energía en cada célula de mi cuerpo.",
                "Yo soy abundancia y todo lo que necesito llega a tiempo.",
                "Yo soy claridad. Sé qué hacer y lo hago.",
                "Yo soy paz aun en medio del ruido.",
                "Yo soy el que decide quién voy a ser hoy.",
                "Yo soy capaz de sostener todo lo que estoy pidiendo."
            )
        ),

        Paquete(
            "Abundancia y dinero",
            "Para trabajar la relación con el dinero.",
            listOf(
                "El dinero llega a mis manos y fluye sin esfuerzo.",
                "Merezco cobrar bien por lo que sé hacer.",
                "Hay suficiente. No estoy compitiendo por migajas.",
                "Cada peso que administro bien atrae más.",
                "Mi trabajo resuelve problemas reales y por eso se paga.",
                "Me abro a recibir de formas que todavía no imagino.",
                "La abundancia empieza con lo que hago hoy, no mañana."
            )
        ),

        Paquete(
            "Gratitud y presencia",
            "Para aterrizar y dejar de correr.",
            listOf(
                "Estoy aquí. Esto es lo único que existe.",
                "Gracias por el cuerpo que me está sosteniendo ahorita.",
                "Gracias por la gente que me quiere aunque no siempre lo diga.",
                "Nada de lo que estoy persiguiendo vale más que este momento.",
                "Ya tengo cosas que alguna vez pedí con desesperación.",
                "Respira. Suelta los hombros. Aquí no falta nada.",
                "El presente es el único lugar donde puedo actuar."
            )
        ),

        Paquete(
            "Enfoque y trabajo profundo",
            "Para volver a la tarea después de la frase.",
            listOf(
                "Una sola cosa. La que importa. Ahora.",
                "El teléfono no se va a ir. La concentración sí.",
                "Terminar vale más que empezar bonito.",
                "No busques la idea perfecta. Avanza con la que tienes.",
                "Dos horas enfocado valen más que ocho distraído.",
                "Si no sabes qué sigue, escribe el siguiente paso más chico.",
                "El trabajo profundo se defiende. Nadie lo va a defender por ti."
            )
        ),

        Paquete(
            "Calma y respiración",
            "Para bajar revoluciones cuando trae uno el nudo.",
            listOf(
                "Inhala cuatro. Sostén cuatro. Exhala seis. Otra vez.",
                "La ansiedad es prisa. No hay prisa.",
                "Puedes sentir miedo y aun así seguir.",
                "Suelta la mandíbula. Baja los hombros. Afloja las manos.",
                "Esto también va a pasar, como todo lo anterior.",
                "No tienes que resolverlo todo hoy.",
                "Tu cuerpo sabe calmarse. Nada más dale un minuto."
            )
        ),

        Paquete(
            "Mentalidad estoica",
            "Inspiradas en Marco Aurelio y Epicteto, escritas en palabras propias.",
            listOf(
                "Lo que no depende de ti, suéltalo. Lo que sí, hazlo bien.",
                "No es el problema el que te tumba, es lo que te dices del problema.",
                "El obstáculo enseña el camino.",
                "Hoy vas a toparte con gente difícil. Ya lo sabes, no te sorprendas.",
                "Vive como si esto fuera lo último que haces, sin drama y sin prisa.",
                "Nadie te puede quitar la forma en que respondes.",
                "Deja de discutir cómo debería ser un hombre bueno. Sé uno."
            )
        )
    )
}
