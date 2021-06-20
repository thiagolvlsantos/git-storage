package io.github.thiagolvlsantos.git.storage.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.github.thiagolvlsantos.git.storage.exceptions.GitStorageException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@Builder
@ToString(onlyExplicitlyIncluded = true)
public class PairValue<T> {
	T annotation;
	private Field field;
	private Method read;
	private Method write;
	@ToString.Include
	private String name;
	@ToString.Include
	private Object value;

	public void set(Object instance, Object value) throws GitStorageException {
		try {
			getWrite().invoke(instance, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new GitStorageException(e.getMessage(), e);
		}
	}

	public Object get(Object instance) throws GitStorageException {
		try {
			return getRead().invoke(instance);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new GitStorageException(e.getMessage(), e);
		}
	}
}