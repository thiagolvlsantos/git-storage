package io.github.thiagolvlsantos.git.storage;

import java.io.File;

public interface IGitIndex {

	String IDS = "ids";

	String KEYS = "keys";

	Long next(File dir);

	<T> void bind(File dir, T instance);

	<T> void unbind(File dir, T instance);

	File directory(File dir, String kind);
}