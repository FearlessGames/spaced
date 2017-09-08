package se.spaced.shared.util.guice.dependencytool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DependencySorter<T> {
	private final Map<T, Collection<T>> dependencies;

	public DependencySorter(Map<T, Collection<T>> dependencies) {
		this.dependencies = dependencies;
	}

	public List<List<VisitResult<T>>> process() {
		HashMap<T, VisitResult<T>> visited = new HashMap<T, VisitResult<T>>();

		for (Map.Entry<T, Collection<T>> entry : dependencies.entrySet()) {
			VisitResult visitResult = visit(entry.getKey(), visited);
			if (visitResult.getState() != VisitResult.State.CLOSED) {
				throw new RuntimeException("Error, state was not closed, but was " + visitResult.getState());
			}
		}

		Set<VisitResult<T>> results = new HashSet<VisitResult<T>>();
		int maxLevel = 0;
		for (VisitResult t : visited.values()) {
			results.add(t);
			maxLevel = Math.max(maxLevel, t.getLevel());
		}

		List<List<VisitResult<T>>> list = new ArrayList<List<VisitResult<T>>>(maxLevel + 1);
		for (int i = 0; i <= maxLevel; i++) {
			list.add(new ArrayList<VisitResult<T>>());
		}

		for (VisitResult<T> result : results) {
			list.get(result.getLevel()).add(result);
		}

		return list;
	}

	private VisitResult visit(T key, Map<T, VisitResult<T>> visited) {
		VisitResult<T> result = visited.get(key);
		if (result == null) {
			//System.out.println("ENTER: " + key);
			result = new VisitResult(VisitResult.State.OPEN, key);
			visited.put(key, result);
			for (T dependency : dependencies.get(key)) {
				//System.out.println("DEP: " + key + " depends on " + dependency);
				VisitResult visitResult = visit(dependency, visited);
				if (visitResult.equals(result)) {
					if (visitResult.getState() != VisitResult.State.OPEN) {
						throw new RuntimeException("Invalid state, expected open but was: " + visitResult.getState());
					}
				} else {
					//System.out.println("DEP: " + key + " depends on " + dependency + " with status " + visitResult);
					if (visitResult.getState() == VisitResult.State.OPEN) {
						merge(result, visitResult, visited);
						result.setState(VisitResult.State.INVALID);
						result = visitResult;
					} else if (visitResult.getState() == VisitResult.State.CLOSED) {
						if (result.getLevel() <= visitResult.getLevel()) {
							result.setLevel(visitResult.getLevel() + 1);
						}
						//System.out.println("DEP: " + key + " depends on " + dependency + " updating level to " + result.getLevel());
					} else {
						throw new RuntimeException("Invalid state!");
					}
				}
			}
			if (result.getState() == VisitResult.State.INVALID) {
				throw new RuntimeException("Invalid state!");
			}
			if (isRoot(key, result)) {
				result.setState(VisitResult.State.CLOSED);
			}
			//System.out.println("EXIT: " + key + ", " + result);
		}
		return result;
	}

	private void merge(VisitResult<T> from, VisitResult<T> to, Map<T, VisitResult<T>> visited) {
		if (to.getLevel() < from.getLevel()) {
			to.setLevel(from.getLevel());
		}
		for (T t : from.getElements()) {
			VisitResult<T> result = visited.get(t);
			to.getElements().add(t);
			visited.put(t, to);
		}
	}

	private boolean isRoot(T key, VisitResult<T> myResult) {
		return myResult.getOwner() == key;
	}
}
