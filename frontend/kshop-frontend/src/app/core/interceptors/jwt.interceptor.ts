import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Adds the JWT saved after login to API requests.
 */
export const jwtInterceptor: HttpInterceptorFn = (request, next) => {
  const token =
    typeof localStorage === 'undefined' ? null : localStorage.getItem('token');

  if (!token || !request.url.startsWith('http://localhost:8080/api')) {
    return next(request);
  }

  return next(
    request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    }),
  );
};
