package io.github.thiagolvlsantos.git.storage.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import io.github.thiagolvlsantos.git.storage.IGitIndex;
import io.github.thiagolvlsantos.git.storage.util.annotations.UtilAnnotations;
import lombok.SneakyThrows;

@Component
public class GitIndexImpl implements IGitIndex {

	@Override
	@SneakyThrows
	public Long next(File dir) {
		Long current = 0L;
		File file = new File(dir, ".current");
		if (file.exists()) {
			current = Long.valueOf(Files.readString(file.toPath()));
		}
		current = current + 1;
		Files.write(file.toPath(), String.valueOf(current).getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		return current;
	}

	@Override
	@SneakyThrows
	public <T> void bind(File dir, T instance) {
		if (instance == null) {
			throw new RuntimeException("Invalid argument: null");
		}
		File index = index(dir);
		if (!index.exists() && !index.mkdirs()) {
			throw new RuntimeException("Could not create index directory: " + index);
		}

		Object[] ids = UtilAnnotations.getIds(instance.getClass(), instance);
		Object[] keys = UtilAnnotations.getKeys(instance.getClass(), instance);

		File id2Keys = ids(dir, ids);
		File id2KeysParent = id2Keys.getParentFile();
		if (!id2KeysParent.exists() && !id2KeysParent.mkdirs()) {
			throw new RuntimeException("Could not create index directory: " + id2KeysParent);
		}
		Files.write(id2Keys.toPath(),
				Stream.of(keys).map(k -> String.valueOf(k)).collect(Collectors.joining("\n")).getBytes(),
				StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

		File keys2Id = keys(dir, keys);
		File keys2IdParent = keys2Id.getParentFile();
		if (!keys2IdParent.exists() && !keys2IdParent.mkdirs()) {
			throw new RuntimeException("Could not create index directory: " + keys2IdParent);
		}
		Files.write(keys2Id.toPath(),
				Stream.of(ids).map(k -> String.valueOf(k)).collect(Collectors.joining("\n")).getBytes(),
				StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}

	private File index(File dir) {
		return new File(dir, ".index");
	}

	@Override
	public <T> void unbind(File dir, T instance) {
		File ids2Keys = ids(dir, UtilAnnotations.getIds(instance.getClass(), instance));
		if (ids2Keys.exists() && !ids2Keys.delete()) {
			throw new RuntimeException("Could no delete entity id->keys: " + ids2Keys);
		}

		File keys2Id = keys(dir, UtilAnnotations.getKeys(instance.getClass(), instance));
		if (keys2Id.exists() && !keys2Id.delete()) {
			throw new RuntimeException("Could no delete entity keys->id: " + keys2Id);
		}
	}

	@Override
	public File directory(File dir, String kind) {
		return new File(index(dir), kind);
	}

	private File ids(File dir, Object... ids) {
		return flatName(directory(dir, "ids"), ids);
	}

	private File keys(File dir, Object... keys) {
		return flatName(directory(dir, "keys"), keys);
	}

	private File flatName(File dir, Object... objs) {
		return new File(dir, Stream.of(objs).map(o -> String.valueOf(o)).collect(Collectors.joining("_")));
	}
}