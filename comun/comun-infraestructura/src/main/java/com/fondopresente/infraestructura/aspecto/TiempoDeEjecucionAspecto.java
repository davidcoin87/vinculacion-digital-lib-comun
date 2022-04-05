package com.fondopresente.infraestructura.aspecto;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TiempoDeEjecucionAspecto {
    private static final Logger LOGGER = LoggerFactory.getLogger(TiempoDeEjecucionAspecto.class);
    private static final String MENSAJE_TIEMPO_CONECCION = "Tiempo de Ejecuccion Proceso: ";

    @Around("@annotation(com.fondopresente.infraestructura.aspecto.LogTiempoDeEjecucion)")
    public Object logTiempoDeEjecucion(ProceedingJoinPoint joinPoint) throws Throwable {

        final long start = System.currentTimeMillis();

        final Object proceed = joinPoint.proceed();

        final long executionTime = System.currentTimeMillis() - start;

        LOGGER.info(MENSAJE_TIEMPO_CONECCION.concat(joinPoint.getSignature().getName() + " ejecutado  en " + executionTime + "ms"));

        return proceed;
    }
}
