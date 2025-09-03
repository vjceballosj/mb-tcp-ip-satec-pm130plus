import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app';
import { routes } from './app/app.routes';

import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';

bootstrapApplication(AppComponent, {
  providers: [
    // 🚦 Routing principal
    provideRouter(routes),

    // 🌐 Cliente HTTP moderno (con soporte fetch API opcional)
    provideHttpClient(withFetch()),

    // 🎭 Animaciones (requerido por Angular Material)
    provideAnimations()
  ]
}).catch(err => console.error(err));

