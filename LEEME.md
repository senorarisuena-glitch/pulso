# Pulso

App de Android para trabajar en ciclos de 25 minutos con bloques de ejercicio,
frases habladas y notificaciones que corren todo el día en segundo plano.

## Cómo sacar el APK sin instalar nada en tu compu

1. Entra a github.com y crea un repositorio nuevo. Puede ser privado.
2. En la página del repo vacío, dale a **uploading an existing file**.
3. Descomprime `pulso.zip` en tu compu y arrastra **todo el contenido**
   (las carpetas `app`, `.github`, y los archivos sueltos) a esa página.
   Importante: sube el contenido, no la carpeta `pulso` completa.
4. Dale a **Commit changes**. GitHub empieza a compilar solo.
5. Ve a la pestaña **Actions**. Espera de 3 a 5 minutos a que la corrida
   termine con una palomita verde.
6. Entra a la corrida y hasta abajo, en **Artifacts**, descarga `pulso-apk`.
7. Descomprime, pasa el `app-debug.apk` a tu teléfono y ábrelo.
   Android te va a pedir permiso para instalar apps de fuentes desconocidas: acéptalo.

Si la corrida sale con tache rojo, entra a la corrida, copia el error
y pásamelo para arreglarlo.

## Al abrir la app la primera vez

1. Acepta el permiso de notificaciones.
2. Dale al botón **Quitar la optimización de batería** y acepta.
   Sin esto, Android puede matar la app después de un rato.
3. Si tu teléfono es Xiaomi, Huawei, Oppo o Samsung, entra a
   Ajustes > Aplicaciones > Pulso y activa "Inicio automático" o
   "Permitir actividad en segundo plano". Cada marca lo llama distinto.
4. Ajusta tiempos, ejercicios y frases. Dale **Guardar cambios**.
5. **Empezar**.

## Cómo se comporta

- Trabaja 25 minutos. Cada 4 minutos suelta una frase: aparece como
  notificación y se escucha. Si la deslizas, se calla y desaparece;
  el ciclo sigue corriendo.
- En el último minuto aparece la cuenta regresiva: 60, 59, 58... hablada
  y en la notificación, con el nombre del bloque que viene.
- Luego arranca el bloque de ejercicios. La notificación trae botones
  **+1 rep**, **Saltar** y **Callar**.
- Los bloques rotan: ciclo 1 usa el bloque 1, ciclo 2 el bloque 2, y así.
- Las repeticiones se guardan por ejercicio y por día.

## Tus propios audios

En cada frase hay un botón **Poner audio**. Escoge un archivo de audio
de tu teléfono (grábalo con la grabadora de voz que ya traes). Cuando toque
esa frase, se escucha tu voz en lugar de la del sistema. El texto que dejes
escrito es lo que aparece en la notificación.

## Sobre las llamadas

La voz usa el mismo canal que las indicaciones de Google Maps, así que
normalmente se escucha durante una llamada. Depende del teléfono y de si
usas altavoz o audífonos: en algunos modelos Android baja o corta la voz
durante llamadas. Si en el tuyo no suena, dímelo y le cambio el canal de audio.
