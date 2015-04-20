import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Cooldown2 implements KeyListener, Runnable {

	public static void main(String[] args) {
		new Cooldown2();
	}

	private Queue<QueuedAction> actionQueue;
	private CooldownMeter2 gcd;
	private CooldownMeter2 arcane;
	private CooldownMeter2 nuke;
	private CooldownMeter2 basic;
	private CooldownMeter2 iWin;

	private int totalDamage;
	private JLabel damageText;
	private JLabel queueText;
	private CooldownMeter2 timer;
	private boolean reset = false;

	public Cooldown2() {
		actionQueue = new ConcurrentLinkedQueue<QueuedAction>() {
			@Override
			public boolean add(QueuedAction queuedAction) {
				QueuedAction prevQueuedAction = peek();
				if (prevQueuedAction == null) {
					return super.add(queuedAction);
				}

				return !prevQueuedAction.getName().equals(queuedAction.getName()) && super.add(queuedAction);

			}
		};

		JFrame frame = new JFrame("Cooldown2");

		GridLayout layout = new GridLayout();

		layout.setColumns(2);
		layout.setRows(7);
		frame.setLayout(layout);

		// row 1
		frame.add(new JLabel("One minute to maximize your damage. Press R to start"));
		damageText = new JLabel();
		frame.add(damageText);
		queueText = new JLabel();
		//frame.add(queueText);
		timer = createMeter(frame, "timer", 60 * 1000, 0, null);


		//JButton start = new JButton("start");
		//frame.add(start);

		gcd = createMeter(frame, "gcd", 500, 100, null);
		basic = createMeter(frame, "basic: 1", 2000, 400, gcd);
		arcane = createMeter(frame, "arcane: 2", 5000, 1000, gcd);
		nuke = createMeter(frame, "nuke: 3", 6000, 1200, gcd);
		iWin = createMeter(frame, "I win: 4", 30000, 6000, gcd);
		//start.addKeyListener(this);
		frame.addKeyListener(this);

		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		new Thread(this).run();
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			long now = System.currentTimeMillis();
			if (reset) {
				reset = false;
				gcd.reset(now);
				basic.reset(now);
				arcane.reset(now);
				nuke.reset(now);
				iWin.reset(now);
				timer.reset(now);
				setDamage(0);
				actionQueue.clear();
				updateQueueText();
			}

			QueuedAction queuedAction = actionQueue.peek();
			if (queuedAction != null) {
				boolean casted = tryCast(now, queuedAction);
				if (casted) {
					actionQueue.remove(queuedAction);
					updateQueueText();
				}
			}

		}
	}

	private CooldownMeter2 createMeter(JFrame frame, String name, long cost, long buffer, CooldownMeter2 gcd) {
		CooldownMeter2 meter = new CooldownMeter2(0, cost, buffer);
		JCooldown2 cooldown = new JCooldown2(meter, gcd);
		cooldown.setToolTipText(name);
		cooldown.setName(name);
		cooldown.setString(name);
		frame.add(new JLabel(name));
		frame.add(cooldown);
		return meter;
	}

	public void keyPressed(KeyEvent arg0) {
		char c = arg0.getKeyChar();
		if (c == 'r') {
			reset = true;
			return;
		}

		if (c == '1') {
			actionQueue.add(new QueuedAction("Basic", basic, 10));
		} else if (c == '2') {
			actionQueue.add(new QueuedAction("Arcane", arcane, 20));
		} else if (c == '3') {
			actionQueue.add(new QueuedAction("Nuke", nuke, 40));
		} else if (c == '4') {
			actionQueue.add(new QueuedAction("iWin", iWin, 100));
		} else {
			System.out.println("unknown key: " + c);
		}
		updateQueueText();

	}

	private void setDamage(int i) {
		totalDamage = i;

	}

	private boolean tryCast(long now, QueuedAction queuedAction) {
		if (timer.getCooldown(now) < timer.getMaxValue() && gcd.canCast(now) && queuedAction.getMeter().canCast(now)) {
			gcd.consumeCooldown(now);
			queuedAction.getMeter().consumeCooldown(now);
			setDamage(totalDamage + queuedAction.getDamage());
			return true;
		}

		return false;
	}

	private void updateQueueText() {
		StringBuilder sb = new StringBuilder();
		sb.append("Damage: ").append(totalDamage);
		sb.append(" Queue: ");
		for (QueuedAction queuedAction : actionQueue) {
			sb.append(queuedAction.getName());
			sb.append(" ");
		}
		damageText.setText(sb.toString());
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

	private class QueuedAction {
		private String name;
		private CooldownMeter2 meter;
		private int damage;

		private QueuedAction(String name, CooldownMeter2 meter, int damage) {
			this.name = name;
			this.meter = meter;
			this.damage = damage;
		}

		public CooldownMeter2 getMeter() {
			return meter;
		}

		public int getDamage() {
			return damage;
		}

		public String getName() {
			return name;
		}
	}
}
