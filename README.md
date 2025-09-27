# README – Prueba técnica Regula Face SDK  

## Descripción de la solución  
La aplicación implementa un flujo sencillo de **captura y comparación facial** utilizando el SDK de Regula Face.  
El usuario puede:  

- Capturar una imagen facial mediante el SDK.  
- Seleccionar otra imagen desde la galería.  
- Comparar ambas imágenes y visualizar el resultado de similitud.  

Se ha priorizado una **estructura limpia y modular del backend** sobre la UI.  

---

## Patrones utilizados  
- **MVVM (Model–View–ViewModel):** separación clara entre UI y lógica.  
- **Repository/Gateway Pattern:** interfaces para abstraer la lógica del SDK y facilitar mocks/tests futuros.  
- **State Holder con `UiState`:** la vista observa estados para una experiencia más fluida.  
- **Coroutines/Flows:** para operaciones asíncronas y reactivas (captura, comparación, conectividad).  

---

## Decisiones técnicas destacadas  
- **Dependencias:** integración manual de los `.aar` del SDK (`api/core-basic/core-match`) para evitar problemas de credenciales Maven.  
- **Gestión de imágenes:** uso de **Coil** en la UI para mostrar imágenes de gran tamaño de forma eficiente.  
- **Control de permisos:** necesario para cámara e internet.  
- **Modularidad:** se priorizó claridad sobre sobreingeniería, manteniendo interfaces simples (`FaceMatcher`, `FaceCaptureLauncher`).  
- **Gestión de errores:** las excepciones del SDK se recogen y se muestran como mensajes dentro de la UI.  
- **MainScreen única:** por tiempo se decidió no añadir navegación adicional, pero dejando preparada la arquitectura para que escale fácilmente.  

---

## Aspectos a mejorar o extender  
- **Nombres de clases/interfaces:** refactorizar para mayor claridad (p. ej. `RegulaFaceMatcher` → `FaceMatcherRegulaImpl`).  
- **ViewModel:** revisar el uso de `viewModelScope` en `init` (no recomendado) y mover la inicialización a un punto más controlado.  
- **Permisos:**  
  - Mejor gestión con **Flow** para conectividad.  
  - Mejor control en el caso de que el usuario rechace los permisos de cámara.  
- **Conectividad:** informar de la necesidad de internet **antes** de que el usuario pulse el botón de comparar, para mejorar la experiencia.  
- **UI/UX:**  
  - Añadir scroll para evitar que los botones se solapen en pantallas pequeñas.  
  - Reemplazar colores por una paleta propia y strings hardcodeados por recursos traducibles.  
  - Habilitar en el futuro la opción de reconocimiento activo.  
- **Arquitectura:**  
  - Modularizar mejor `MainActivity`, que actualmente está sobrecargada.  
  - Extraer la lógica de permisos y captura en componentes reutilizables.  

---
