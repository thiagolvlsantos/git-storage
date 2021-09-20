package io.github.thiagolvlsantos.git.storage;

import java.io.File;
import java.lang.reflect.AnnotatedType;

public interface IGitSerializer {

	Object decode(String data, AnnotatedType type);

	String encode(Object instance);

	<T> T readValue(File file, Class<T> type);

	<T> void writeValue(File file, T instance);

}
