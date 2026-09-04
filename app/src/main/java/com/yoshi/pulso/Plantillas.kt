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

    val paquetes: List<Paquete> = listOf(

        Paquete(
            "Motivación y disciplina",
            "Para los días en que no tienes ganas.",
            listOf(
                "No estás esperando el momento. El momento es este.",
                "La disciplina es acordarte de lo que quieres de verdad.",
                "Nadie viene a rescatarte, y está bien. Puedes tú.",
