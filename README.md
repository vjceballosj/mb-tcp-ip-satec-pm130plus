# mb-tcp-ip-satec-pm130plus
Fullstack Spring - JAVA 21 &amp; Angular 17 ModbusTCP/IP PM130PLUS SATEC

Estrcutura Back (Java Spring Boot 21):

src/main/java/com/modbus/pm130plus
│
├── Pm130plusApplication.java       # Clase principal
│
├── config/                         # Configuración
│   ├── ModbusConfig.java
│   └── Pm130Properties.java
│
├── model/                          # Entidades JPA
│   └── MeterReading.java
│
├── repository/                     # Repositorios JPA
│   └── MeterReadingRepository.java
│
├── service/                        # Servicios (polling, lógica)
│   └── Pm130PollerService.java
│
└── controller/                     # API REST
    └── MeterReadingController.java

Estrcutura Front (Angular 22+, material):

src/app/
│
├── core/                 # Servicios globales, guards, interceptores
│   ├── services/
│   │   └── api.service.ts
│   └── interceptors/
│       └── auth.interceptor.ts
│
├── shared/               # Componentes reutilizables, pipes, directivas
│   └── components/
│       └── navbar/
│           ├── navbar.component.ts
│           └── navbar.component.scss
│
├── features/             # Funcionalidades principales
│   └── meter-readings/   # Lecturas del PM130 Plus
│       ├── meter-reading-list.component.ts
│       └── meter-reading-list.component.scss
│
├── app.routes.ts         # Rutas principales
└── app.component.ts

