package se.fearless.bender;

import org.junit.Before;
import org.junit.Test;
import se.mockachino.annotations.*;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class CommitLogTest {
	private CommitLog commitLog;

	@Mock
	private Commit commit1;
	@Mock
	private Commit commit2;
	@Mock
	private Commit commit3;
	@Mock
	private Commit commit4;

	@Before
	public void setup() {
		setupMocks(this);
		commitLog = new CommitLog(3);
	}

	@Test
	public void testAddCommits() {
		commitLog.addCommit(commit1);
	}

	@Test
	public void testGetLatest() {
		commitLog.addCommit(commit1);
		List<Commit> list = commitLog.getLast(1);
		assertEquals(commit1, list.get(0));
	}

	@Test
	public void testGetLastTwo() {
		commitLog.addCommit(commit1);
		commitLog.addCommit(commit2);
		List<Commit> list = commitLog.getLast(2);
		assertEquals(commit1, list.get(0));
		assertEquals(commit2, list.get(1));
	}

	@Test
	public void testGetLastTwoWhenWeHaveMoreThenTwo() {
		commitLog.addCommit(commit1);
		commitLog.addCommit(commit2);
		commitLog.addCommit(commit3);
		List<Commit> list = commitLog.getLast(2);
		assertTrue(list.size() == 2);
		assertEquals(commit2, list.get(0));
		assertEquals(commit3, list.get(1));
	}

	public void testGetMoreThenThereIs() {
		commitLog.addCommit(commit1);
		commitLog.addCommit(commit2);
		List<Commit> list = commitLog.getLast(3);
		assertTrue(list.size() == 2);
		assertEquals(commit1, list.get(0));
		assertEquals(commit2, list.get(1));
	}

	@Test
	public void testMaxQueueSize() {
		commitLog.addCommit(commit1);
		commitLog.addCommit(commit2);
		commitLog.addCommit(commit3);
		commitLog.addCommit(commit4);
		List<Commit> list = commitLog.getLast(100);
		assertEquals(3, list.size());
		assertEquals(commit2, list.get(0));
	}
}
