<h1 align="center">💰 FinanzApp</h1>
<p>Aplicación Android para gestionar ingresos y gastos personales, diseñada para ayudar a los usuarios a tener un mejor control de sus finanzas mediante una interfaz moderna y una arquitectura robusta.</p>
<p>Este proyecto fue desarrollado utilizando tecnologías modernas de Android y siguiendo buenas prácticas de arquitectura, con el objetivo de construir una aplicación escalable, mantenible y fácil de extender.</p>

<h3>📱 Preview</h3>
<p align="center">
  <img src="https://github.com/user-attachments/assets/8e3c5d14-3296-495d-9324-a2d6900143b4" width="128"/>
  <img src="https://github.com/user-attachments/assets/88cfaca5-7653-4822-9c53-6b9898832124" width="128"/>
  <img src="https://github.com/user-attachments/assets/160f7494-5851-47c5-ac72-4dbc4450c004" width="128"/>
  <img src="https://github.com/user-attachments/assets/8bb20055-709e-432a-9db8-43c50eba7552" width="128"/>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/8cd55299-8ec0-4be0-822f-05e4e6afeeda" width="128"/>
  <img src="https://github.com/user-attachments/assets/dee2ed32-8b17-48c4-a63d-2d59f01432c7" width="128"/>
  <img src="https://github.com/user-attachments/assets/cfe7bc13-ac39-457a-8f33-80c5601e1992" width="128"/>
  <img src="https://github.com/user-attachments/assets/95cd1730-1005-4889-9f58-40103d941551" width="128"/>
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/cd5ac47d-522c-4b7b-acba-85c461be9377" width="128"/>
  <img src="https://github.com/user-attachments/assets/b44f2dca-f75e-4f97-8976-02ea49d66ac2" width="128"/>
  <img src="https://github.com/user-attachments/assets/29175304-2a46-4d34-8258-6bbdb02d6b1f" width="128"/>
</p>


<h3>✨ Features</h3>
<ul>
  <li>Registrar ingresos</li>
  <li>Registrar gastos</li>
  <li>Visualizar historial de transacciones</li>
  <li>Persistencia de datos local</li>
  <li>Interfaz moderna con Jetpack Compose</li>
  <li>Manejo eficiente del estado con ViewModel y Flow</li>
</ul>

<h3>🧰 Tech Stack</h3>
<table>
  <thead>
    <tr>
      <td align="center"><strong>Tecnología</strong></td>
      <td align="center"><strong>Uso</strong></td>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Kotlin</td>
      <td>Lenguaje principal</td>
    </tr>
    <tr>
      <td>Jetpack Compose</td>
      <td>UI declarativa</td>
    </tr>
    <tr>
      <td>MVVM</td>
      <td>Arquitectura de presentación</td>
    </tr>
    <tr>
      <td>Clean Architecture</td>
      <td>Separación de capas</td>
    </tr>
    <tr>
      <td>Room</td>
      <td>Base de datos local</td>
    </tr>
    <tr>
      <td>DataStore</td>
      <td>Base de datos local</td>
    </tr>
    <tr>
      <td>Inyección de Dependencias (DI)</td>
      <td>Gestión de dependencias</td>
    </tr>
    <tr>
      <td>Corrutinas & Flow</td>
      <td>Programación asíncrona</td>
    </tr>
  </tbody>
</table>

<h3>🏗️ Arquitectura</h3>
<p>El proyecto sigue Clean Architecture, separando la aplicación en tres capas principales:</p>
<ol>
  <li>
    <h4>Presentation Layer</h4>
    <ul>
      <li>UI (Jetpack Compose)</li>
      <li>ViewModels</li>
    </ul>
  </li>
  <li>
    <h4>Domain Layer</h4>
    <ul>
      <li>Models</li>
      <li>UseCases</li>
      <li>Repository Interfaces</li>
    </ul>
  </li>
  <li>
    <h4>Data Layer</h4>
    <ul>
      <li>Repository Implementations</li>
      <li>Room Database</li>
      <li>Data Sources</li>
    </ul>
  </li>
</ol>

<h3>📂 Estructura del proyecto</h3>

```Estructura
app
│
├── data
│   ├── local
│   │   ├── dao
│   │   ├── entity
│   │   └── database
│   │
│   └── repository
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── presentation
│   ├── screens
│   ├── components
│   └── viewmodel
│
└── di
```

<h3>⚙️ Instalación</h3>

<ol>
  <li>
    Clonar el repositorio
    
```Bash

git clone https://github.com/Luis1812/FinanzApp.git

```
  </li>
  <li>Abrir el proyecto en Android Studio</li>
  <li>Ejecutar la app en un emulador o dispositivo físico</li>
</ol>

<h2 align="center">👨‍💻 Autor</h2>

<p align="center">
  <b>Luis Castillo</b><br>
  Android Developer<br>
  Especializado en Kotlin, Jetpack Compose y arquitecturas modernas<br><br>
</p>

<p align="center">
  <a href="https://github.com/Luis1812">
    <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"/>
  </a>

  <a href="https://www.linkedin.com/in/luis-alberto-castillo-cuyutupa-1a4116183">
    <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"/>
  </a>
</p>

