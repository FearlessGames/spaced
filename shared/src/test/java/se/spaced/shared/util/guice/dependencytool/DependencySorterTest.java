package se.spaced.shared.util.guice.dependencytool;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DependencySorterTest {

	@Test
	public void testSimple() {
		Map<Integer, Collection<Integer>> dependencyMap = new HashMap<Integer, Collection<Integer>>();
		dependencyMap.put(1, Collections.emptyList());
		dependencyMap.put(2, Arrays.asList(1));
		dependencyMap.put(3, Arrays.asList(1));
		dependencyMap.put(4, Arrays.asList(1, 2));
		dependencyMap.put(5, Arrays.asList(1, 2, 3, 4));
		dependencyMap.put(6, Arrays.asList(2, 3));

		DependencySorter<Integer> sorter = new DependencySorter<Integer>(dependencyMap);
		List<List<VisitResult<Integer>>> list = sorter.process();
		for (List<VisitResult<Integer>> visitResults : list) {
			for (VisitResult visitResult : visitResults) {
				//System.out.println(visitResult);
			}
		}

		assertEquals(4, list.size());
		assertEquals(1, list.get(0).size());
		assertEquals(2, list.get(1).size());
		assertEquals(2, list.get(2).size());
		assertEquals(1, list.get(3).size());
	}

	@Test
	public void testSimpleCycle() {
		Map<Integer, Collection<Integer>> dependencyMap = new HashMap<Integer, Collection<Integer>>();
		dependencyMap.put(1, Arrays.asList(2));
		dependencyMap.put(2, Arrays.asList(1));

		DependencySorter<Integer> sorter = new DependencySorter<Integer>(dependencyMap);
		List<List<VisitResult<Integer>>> list = sorter.process();
		for (List<VisitResult<Integer>> visitResults : list) {
			for (VisitResult visitResult : visitResults) {
				//System.out.println(visitResult);
			}
		}

		assertEquals(1, list.size());
		assertEquals(1, list.get(0).size());
		assertEquals(2, list.get(0).get(0).getElements().size());
		assertTrue(list.get(0).get(0).getElements().contains(1));
		assertTrue(list.get(0).get(0).getElements().contains(2));

		assertEquals(0, list.get(0).get(0).getLevel());
	}

	@Test
	public void testSimpleCycle2() {
		Map<Integer, Collection<Integer>> dependencyMap = new HashMap<Integer, Collection<Integer>>();
		dependencyMap.put(1, Arrays.asList(2));
		dependencyMap.put(2, Arrays.asList(1));
		dependencyMap.put(3, Arrays.asList(1));

		DependencySorter<Integer> sorter = new DependencySorter<Integer>(dependencyMap);
		List<List<VisitResult<Integer>>> list = sorter.process();
		for (List<VisitResult<Integer>> visitResults : list) {
			for (VisitResult visitResult : visitResults) {
				//System.out.println(visitResult);
			}
		}

		assertEquals(2, list.size());
		assertEquals(1, list.get(0).size());
		assertEquals(1, list.get(1).size());

		assertEquals(2, list.get(0).get(0).getElements().size());
		assertTrue(list.get(0).get(0).getElements().contains(1));
		assertTrue(list.get(0).get(0).getElements().contains(2));

		assertEquals(1, list.get(1).get(0).getElements().size());
		assertTrue(list.get(1).get(0).getElements().contains(3));

		assertEquals(0, list.get(0).get(0).getLevel());
		assertEquals(1, list.get(1).get(0).getLevel());
	}

	@Test
	public void testSimpleCycle3() {
		Map<Integer, Collection<Integer>> dependencyMap = new HashMap<Integer, Collection<Integer>>();
		dependencyMap.put(1, Arrays.asList(2, 3));
		dependencyMap.put(2, Arrays.asList(1));
		dependencyMap.put(3, Arrays.asList(1));

		DependencySorter<Integer> sorter = new DependencySorter<Integer>(dependencyMap);
		List<List<VisitResult<Integer>>> list = sorter.process();
		for (List<VisitResult<Integer>> visitResults : list) {
			for (VisitResult visitResult : visitResults) {
				//System.out.println(visitResult);
			}
		}

		assertEquals(1, list.size());
		assertEquals(1, list.get(0).size());

		assertEquals(3, list.get(0).get(0).getElements().size());
		assertTrue(list.get(0).get(0).getElements().contains(1));
		assertTrue(list.get(0).get(0).getElements().contains(2));
		assertTrue(list.get(0).get(0).getElements().contains(3));

		assertEquals(0, list.get(0).get(0).getLevel());
	}

	@Test
	public void testComplexCycle() {
		Map<Integer, Collection<Integer>> dependencyMap = new HashMap<Integer, Collection<Integer>>();
		dependencyMap.put(1, new ArrayList<Integer>());
		dependencyMap.put(2, Arrays.asList(1, 3));
		dependencyMap.put(3, Arrays.asList(2));
		dependencyMap.put(4, Arrays.asList(2));
		dependencyMap.put(5, Arrays.asList(4, 6));
		dependencyMap.put(6, Arrays.asList(5));
		dependencyMap.put(7, Arrays.asList(2));

		DependencySorter<Integer> sorter = new DependencySorter<Integer>(dependencyMap);
		List<List<VisitResult<Integer>>> list = sorter.process();
		for (List<VisitResult<Integer>> visitResults : list) {
			for (VisitResult visitResult : visitResults) {
				//System.out.println(visitResult);
			}
		}

		assertEquals(4, list.size());

		assertEquals(1, list.get(0).size());
		assertEquals(1, list.get(0).get(0).getElements().size());

		assertEquals(1, list.get(1).size());
		assertEquals(2, list.get(1).get(0).getElements().size());

		assertEquals(2, list.get(2).size());
		assertEquals(1, list.get(2).get(0).getElements().size());

		assertEquals(1, list.get(3).size());
		assertEquals(2, list.get(3).get(0).getElements().size());
	}

}
