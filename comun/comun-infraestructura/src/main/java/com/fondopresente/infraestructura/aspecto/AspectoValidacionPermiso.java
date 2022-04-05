package com.fondopresente.infraestructura.aspecto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fondopresente.dominio.excepcion.ExcepcionAutorizacion;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Aspect
@Configuration
public class AspectoValidacionPermiso {

	@Before("@annotation(ValidacionPermiso)")
	public void esValido(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		ValidacionPermiso anotacion = signature.getMethod().getAnnotation(ValidacionPermiso.class);
		String[] nombreModulos = anotacion.permisos();

		ObjectMapper mapper = new ObjectMapper();

		@SuppressWarnings("unchecked")
		List<String> componentesTemp = (List<String>) RequestContextHolder.getRequestAttributes()
				.getAttribute("perfiles", RequestAttributes.SCOPE_REQUEST);

		Predicate<String> predicate =  p -> Arrays.asList(nombreModulos).contains(p);

		if (componentesTemp == null || componentesTemp.isEmpty()) {
			throw new ExcepcionAutorizacion(ExcepcionAutorizacion.MENSAJE_PERFIL_INVALIDO);
		}

		List<String> componentes = mapper.convertValue(componentesTemp, new TypeReference<List<String>>() {
		}).stream().filter(Objects::nonNull).filter(predicate::test)
				.collect(Collectors.toList());

		if (componentes.isEmpty()) {
			throw new ExcepcionAutorizacion(ExcepcionAutorizacion.MENSAJE_PERFIL_INVALIDO);
		}
	}

}
