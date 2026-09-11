package com.yoshi.pulso

/**
 * Plantillas listas. Una rutina llena los tres bloques de golpe.
 * Un paquete de frases reemplaza o agrega a la lista de frases del perfil.
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
            "Mis frases",
            "Las frases originales, tal como las escribiste.",
            listOf(
                "Respira. Concéntrate. Recuerda quién eres y a dónde vas. Eres un victor, y la victoria está dentro de ti, no allá afuera esperándote. Eres un vencedor. No porque nunca te caigas, sino porque siempre te vuelves a levantar.",
                "Agradece. Conecta con tu ser superior. 1, 1, 9, 8, 1. Dios habita en ti, en tu respiración, en tus manos, en lo que estás construyendo justo ahora. No estás solo en esto y nunca lo has estado.",
                "Yo soy la fuente en completa expresión. Lo integro, lo comparto, libero todo límite. Manifiesto la realidad perfecta aquí y ahora, en este momento, en este cuerpo, en esta vida que estoy eligiendo.",
                "Tu percepción cambia tu realidad. Cambia cómo estás percibiendo las cosas hacia lo positivo, porque tu mente es un proyector y afuera solo ves lo que traes adentro. Tu desafío es ser millonario, estar presente y disfrutar de una hermosa familia. Los problemas y los obstáculos no son castigos, son oportunidades disfrazadas.",
                "Yo soy las riquezas de Dios fluyendo a mis manos y a mi uso, que nada ni nadie puede detener. Lo que es mío llega a tiempo, por el camino correcto, y llega para quedarse.",
                "Estás creando una nueva identidad. Obsérvala. Obsérvala con calma, sin exigirle que ya esté terminada. Agradécele. Agradécele por aparecer, aunque todavía sea frágil, aunque a ratos se te olvide. Ya empezó.",
                "Ángeles y guías, les doy permiso de asistirme en todas las áreas de mi vida. Límpienme y protéjanme de toda negatividad, la de afuera y la que yo mismo me genero. Acompáñenme hoy, en lo grande y en lo pequeño.",
                "5, 2, 0. 7, 4, 1, 8."
            )
        ),

        Paquete(
            "Antinarcisismo",
            "Duras y honestas. Para reconocer el patrón antes de repetirlo.",
            listOf(
                "Los arranques de drama y las exigencias de definiciones en caliente no son amor. Son la forma desesperada de mantener el foco en ti para no aguantar el silencio ni la indiferencia. Cuando sientas esa urgencia de exigir una respuesta ya, esa es justo la señal de esperar. El silencio no te está matando.",
                "Cuando te topas con el vacío de su rechazo, lo que asusta no es perderla a ella. Es quedarte solo con el trabajo que tienes pendiente contigo. Ese trabajo no lo puede hacer ella ni lo puede hacer la relación: te toca a ti, y se hace despacio.",
                "Deja de operar bajo esa transacción oculta donde te portas como un santo y tragas saliva esperando que, como recompensa, ella te devuelva el estatus, el aplauso y el perdón. Lo que haces esperando cobro no es un cambio, es una factura con retraso.",
                "Cada vez que montas un espectáculo, te bajas de la camioneta o le exiges que te valide, estás demostrando que el cambio superficial se cae en cuanto la presión sube y las cosas no salen según tu guion. La prueba no son los días buenos. La prueba es hoy, cuando duele.",
                "Le estabas pidiendo que fuera tu terapeuta, tu espejo y tu guía correctora. Le estabas delegando a la mujer que lastimaste la tarea de sostener tu conciencia. Esa carga no es de ella. Busca a alguien capacitado para eso y quítasela de encima.",
                "El costo de seguir forzando una reconciliación desde este lugar es brutal: vas a seguir desgastando tu energía, dándoles a tus hijas un entorno tenso, y volviéndote una presencia resentida en tu propia casa. El ambiente de esa casa hoy depende de ti.",
                "Cumple con tu parte económica y con tus deberes de padre con absoluta dignidad. Pero deja de mendigar migajas de afecto e intimidad de una mujer que te está pidiendo a gritos un espacio que tú te niegas a soltar. Cumplir no te da derecho a cobrar cercanía.",
                "La culpa real no se limpia con discursos bonitos ni con promesas de redención de última hora. Se quema lentamente, con una consistencia silenciosa que no cobra facturas ni exige testigos. Nadie tiene que enterarse de que cambiaste. Se nota solo, con el tiempo.",
                "Cuando notes que las aguas se calman y tu cabeza empiece a susurrarte que ya cambiaste suficiente, o que ella ya debería valorarte, reconoce la trampa. Es el mismo patrón de siempre buscando cobrar su comisión justo cuando bajaste la guardia.",
                "Deja de buscar una respuesta rápida de sí o no para calmar tu ansiedad. Esa exigencia de certeza es el torniquete que termina de asfixiar cualquier posibilidad de sanación. Aguanta la incertidumbre. Ahí es donde se construye la confianza que rompiste.",
                "El dolor de saber que ella estuvo con otros no es amor herido. Es la parte de ti que quiere seguir teniendo el control del tablero y ser el centro de su mundo. Reconócelo por lo que es, y luego regresa a lo tuyo.",
                "Dejar de usar el despecho como combustible significa aceptar que su vida privada ya no te pertenece. Rascarle a esa herida solo demuestra que todavía quieres poseerla en lugar de respetarla. Respetarla se ve así: no preguntar, no revisar, no imaginar."
            )
        ),

        Paquete(
            "Motivación y disciplina",
            "Para los días en que no traes ganas de nada.",
            listOf(
                "No estás esperando el momento correcto. El momento correcto es este, con todo y el cansancio, con todo y las ganas de no hacerlo. El momento perfecto no existe: existe el que aprovechas y el que dejas pasar.",
                "La disciplina no es castigarte. La disciplina es acordarte de lo que quieres de verdad cuando el impulso de hoy te está jalando hacia otro lado. Es tu yo de mañana pidiéndote un favor.",
                "Nadie viene a rescatarte, y está bien que sea así. Significa que no dependes de que alguien más se acuerde de ti, ni de que alguien más crea en ti. Puedes tú. Siempre pudiste.",
                "Lo que hoy se te hace pesado, en tres meses va a ser lo normal. Tu cuerpo se acostumbra, tu cabeza se acostumbra. Lo único que necesita es que no lo sueltes justo cuando empieza a doler.",
                "Hazlo cansado. Hazlo con flojera. Hazlo aunque lo hagas mal y aunque lo hagas a medias. Lo que cuenta es que hoy no rompiste la cadena. Mañana lo haces mejor.",
                "Cada vez que cumples contigo, te vuelves un poco más confiable ante tus propios ojos. Y esa confianza es la que después usas para cosas más grandes. No estás haciendo ejercicio: te estás demostrando algo.",
                "El que sigue cuando ya nadie está viendo es el que gana. No el más rápido ni el más fuerte, sino el que no se detuvo cuando dejó de haber aplausos, porque nunca los estuvo buscando."
            )
        ),

        Paquete(
            "Autoestima",
            "Para dejar de hablarte como si fueras tu propio enemigo.",
            listOf(
                "Mereces el mismo respeto que le das a los demás. Te desvives por no fallarle a nadie y contigo eres implacable. Trátate como tratas a la gente que quieres, aunque al principio te sienta raro.",
                "Tu valor no depende de lo que produjiste hoy. No eres una máquina que vale por su rendimiento. Vales dormido, vales cansado, vales el día que no lograste nada. Eso no se negocia con nadie.",
                "Háblate como le hablarías a alguien que amas. Si un amigo te contara lo que tú te dices en la cabeza, no lo tolerarías. Entonces no lo toleres cuando la voz es la tuya.",
                "Estás aprendiendo, y aprender se ve exactamente así: torpe, lento, con retrocesos. Eso no es lo mismo que estar fallando. Nadie que esté cambiando de verdad lo hace de forma limpia.",
                "Tienes derecho a ocupar espacio. A decir lo que piensas, a pedir lo que necesitas, a poner un límite sin dar diez explicaciones. No estás molestando por existir.",
                "Lo que sientes es válido aunque nadie más lo entienda, aunque no sepas explicarlo, aunque te digan que estás exagerando. No necesitas la aprobación de nadie para tener derecho a tu propia experiencia.",
                "No tienes que ser perfecto para ser suficiente. La versión tuya que exiges es una que nunca ha existido y que nadie te pidió. La que sí existe, la de hoy, ya alcanza."
            )
        ),

        Paquete(
            "Elevar la vibración",
            "Para cambiar tu estado interno en el momento.",
            listOf(
                "Respira profundo y suelta despacio. Todo se acomoda desde la calma, nunca desde la prisa. Lo que decidas apurado lo vas a tener que corregir después; lo que decidas en paz se sostiene solo.",
                "Estás en sintonía con lo bueno que ya viene en camino. No tienes que jalarlo ni forzarlo. Tu único trabajo es no cerrarte, no amargarte, no ponerte en el lugar donde no puede encontrarte.",
                "Suelta lo pesado. Eso que traes en el pecho desde la mañana no es tuyo, o si lo fue ya cumplió su función. No lo tienes que cargar todo el día para demostrar que te importa.",
                "Tu energía abre puertas antes de que digas una palabra. La gente siente cómo llegas mucho antes de escuchar lo que traes que decir. Cuida con qué entras a los lugares.",
                "Donde pones tu atención, ahí crece la vida. Lo que revisas todos los días se hace grande, sea la queja o sea el plan. Escoge bien qué estás regando con tu tiempo.",
                "Agradece antes de tener. Así se llama la fe: dar las gracias por lo que todavía no ves, actuar como si ya viniera, y sostener esa certeza cuando nada afuera te la confirma.",
                "Estás alineado. Estás en paz. Estás exactamente en el lugar donde tienes que estar hoy, aunque no sea donde querías llegar. El camino no se ve completo desde la mitad."
            )
        ),

        Paquete(
            "Yo soy",
            "Decretos en primera persona y en tiempo presente.",
            listOf(
                "Yo soy la fuente en completa expresión, aquí y ahora. No estoy esperando permiso, no estoy esperando el momento. Lo que soy ya está sucediendo mientras respiro.",
                "Yo soy salud, fuerza y energía en cada célula de mi cuerpo. Mi cuerpo sabe repararse, sabe sostenerme, sabe llevarme a donde tengo que ir. Confío en él y lo cuido.",
                "Yo soy abundancia, y todo lo que necesito llega a tiempo. Ni antes ni después. No me falta, no me sobra: llega justo cuando lo puedo recibir y sostener.",
                "Yo soy claridad. Sé qué hacer y lo hago. Cuando no lo sé, me quedo quieto hasta que se aclara, y esa quietud también es parte de saber.",
                "Yo soy paz aun en medio del ruido. No necesito que todo se calme afuera para calmarme adentro. La paz no me la da el entorno, me la doy yo.",
                "Yo soy el que decide quién voy a ser hoy. No lo que me pasó, no lo que hice antes, no lo que otros esperan. Hoy elijo otra vez, y puedo elegir distinto.",
                "Yo soy capaz de sostener todo lo que estoy pidiendo. No pido lo que no podría cuidar. Me estoy preparando para recibirlo y para no perderlo cuando llegue."
            )
        ),

        Paquete(
            "Abundancia y dinero",
            "Para trabajar tu relación con el dinero.",
            listOf(
                "El dinero llega a mis manos y fluye sin esfuerzo. No lo persigo con angustia ni lo agarro con miedo. Entra, hace su trabajo, y vuelve a entrar porque sé moverlo.",
                "Merezco cobrar bien por lo que sé hacer. Lo que ofrezco tiene valor y no tengo que disculparme por ponerle precio. Regalar mi trabajo no me hace más noble, me hace más pobre.",
                "Hay suficiente. No estoy compitiendo por migajas ni peleándome un lugar que ya estaba ocupado. El mundo es más grande que el miedo que me contaron.",
                "Cada peso que administro bien atrae más. No se trata de cuánto entra, sino de qué hago con lo que entra. La abundancia empieza en el orden, no en la cantidad.",
                "Mi trabajo resuelve problemas reales, y por eso se paga. No estoy pidiendo un favor cuando cobro. Estoy intercambiando algo que sirve por algo que necesito.",
                "Me abro a recibir de formas que todavía no imagino. No tiene que llegar por donde yo creo. Suelto el control sobre el cómo y me quedo firme en el qué.",
                "La abundancia empieza con lo que hago hoy, no mañana. No con el negocio que voy a poner, no cuando se acomoden las cosas. Con lo de hoy, con lo que tengo en la mano ahorita."
            )
        ),

        Paquete(
            "Gratitud y presencia",
            "Para aterrizar y dejar de correr.",
            listOf(
                "Estoy aquí. Esto es lo único que existe. El resto es memoria o es imaginación, y ninguna de las dos está pasando ahora. Vuelve al cuerpo, vuelve a la respiración, vuelve a este cuarto.",
                "Gracias por el cuerpo que me está sosteniendo ahorita. Late sin que se lo pida, respira mientras yo pienso en otras cosas, me carga todos los días sin quejarse. Hoy lo reconozco.",
                "Gracias por la gente que me quiere, aunque no siempre sepa demostrarlo y aunque yo no siempre sepa recibirlo. No estoy tan solo como se siente en los días malos.",
                "Nada de lo que estoy persiguiendo vale más que este momento. Voy a llegar y va a haber otra cosa que perseguir. Si no aprendo a estar aquí, no voy a saber estar allá tampoco.",
                "Ya tengo cosas que alguna vez pedí con desesperación. Se me olvidan porque se volvieron normales. Hoy me acuerdo de al menos una y le doy las gracias como el día que la pedí.",
                "Respira. Suelta los hombros. Afloja la mandíbula. Aquí, en este segundo, no falta nada. El problema que traes en la cabeza no está sucediendo en esta habitación.",
                "El presente es el único lugar donde puedo actuar. En el pasado ya no alcanzo nada y en el futuro todavía no llego. Todo mi poder está en lo que haga en los próximos cinco minutos."
            )
        ),

        Paquete(
            "Enfoque y trabajo profundo",
            "Para volver a la tarea justo después de la frase.",
            listOf(
                "Una sola cosa. La que importa. Ahora. No la lista completa, no todo el proyecto: la siguiente acción concreta. Escríbela si hace falta y ve por ella.",
                "El teléfono no se va a ir a ningún lado. La concentración sí. Tardas veinte minutos en recuperarla y la pierdes en dos segundos. Piensa si vale la pena ese cambio.",
                "Terminar vale más que empezar bonito. Un trabajo entregado a medias supera a uno perfecto que sigue en tu cabeza. Lo terminado se puede mejorar; lo que no existe, no.",
                "No busques la idea perfecta. Avanza con la que tienes, aunque sepas que es mejorable. La claridad llega haciendo, no pensando: se te va a aclarar a la mitad del camino.",
                "Dos horas enfocado valen más que ocho distraído. No midas el día por las horas que estuviste sentado, sino por los ratos en que de verdad estuviste ahí.",
                "Si no sabes qué sigue, escribe el paso más chico que se te ocurra. Tan chico que dé risa. La parálisis casi siempre es que el siguiente paso lo estás pensando demasiado grande.",
                "El trabajo profundo se defiende. Nadie lo va a defender por ti, ni tu familia, ni tus clientes, ni tus notificaciones. Si no cierras la puerta tú, no se cierra."
            )
        ),

        Paquete(
            "Calma y respiración",
            "Para bajar revoluciones cuando traes el nudo.",
            listOf(
                "Inhala contando cuatro. Sostén cuatro. Exhala contando seis. Otra vez. La exhalación larga es la que le avisa al cuerpo que puede bajar la guardia. Dale tres rondas antes de seguir.",
                "La ansiedad es prisa, nada más. Es el cuerpo tratando de resolver hoy algo que no toca hoy. No hay prisa. Lo urgente casi nunca es tan urgente como se siente.",
                "Puedes sentir miedo y aun así seguir. Nadie hace las cosas importantes con el miedo apagado. Lo haces con el miedo puesto, y así cuenta igual, o cuenta más.",
                "Suelta la mandíbula. Baja los hombros. Afloja las manos. Llevas horas apretando cosas que no hacía falta apretar, y tu cuerpo se cansa de sostener esa tensión.",
                "Esto también va a pasar, como pasó todo lo anterior. Ninguna de las cosas que te tuvieron así antes sigue teniéndote así hoy. Se te olvida porque cuando duele parece permanente.",
                "No tienes que resolverlo todo hoy. Hoy nada más te toca la parte de hoy. Lo demás va a seguir ahí mañana y vas a tener más fuerza para verlo.",
                "Tu cuerpo sabe calmarse solo, es lo que hace todo el tiempo sin que te enteres. Nada más dale un minuto sin interrumpirlo, sin pensarle, sin exigirle que sea rápido."
            )
        ),

        Paquete(
            "Mentalidad estoica",
            "Inspiradas en Marco Aurelio y Epicteto, escritas en palabras propias.",
            listOf(
                "Lo que no depende de ti, suéltalo. Lo que sí depende de ti, hazlo bien. Casi todo tu sufrimiento vive en la frontera entre esas dos cosas, tratando de controlar lo que nunca fue tuyo.",
                "No es el problema el que te tumba, es lo que te dices del problema. Dos personas viven lo mismo y una se levanta y la otra no. La diferencia está en la historia que se cuentan.",
                "El obstáculo enseña el camino. Lo que se te atraviesa no está impidiendo tu avance: está mostrándote por dónde tienes que crecer. Si no hubiera resistencia, no habría fuerza.",
                "Hoy te vas a topar con gente difícil, con groserías y con gente que no cumple. Ya lo sabes de antemano, así que no te agarre de sorpresa ni te arruine el día.",
                "Vive como si esto fuera lo último que haces, pero sin drama y sin prisa. No significa correr: significa hacerlo con atención, como si valiera la pena hacerlo bien.",
                "Te pueden quitar el trabajo, el dinero, la salud y la razón. Lo único que nadie te puede quitar es la forma en que respondes a lo que te pasa. Ahí vives tú.",
                "Deja de discutir cómo debería ser un hombre bueno. Sé uno. Las horas que gastas explicando en quién te vas a convertir son las mismas que podrías gastar convirtiéndote."
            )
        )
    )
}
