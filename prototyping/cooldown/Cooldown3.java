import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class Cooldown3 extends KeyAdapter {

	public static void main(String[] args) {
		new Cooldown3();
	}

	private CooldownMeter2 gcd;
	private CooldownMeter2 arcane;
	private CooldownMeter2 nuke;
	private CooldownMeter2 basic;
	private CooldownMeter2 iWin;
	private int totalDamage;
	private JLabel damageText;
	private CooldownMeter2 timer;

	Cooldown3() {
		JFrame frame = new JFrame("Cooldown");

		GridLayout layout = new GridLayout();

		layout.setColumns(2);
		layout.setRows(7);
		frame.setLayout(layout);

		// row 1
		frame.add(new JLabel("One minute to maximize your damage. Press R to start"));
		damageText = new JLabel();
		frame.add(damageText);
		timer = createMeter(frame, "timer", 60*1000, 0, null);


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

	@Override
	public void keyPressed(KeyEvent arg0) {
		long now = System.currentTimeMillis();
		char c = arg0.getKeyChar();
		if (c == 'r') {
			gcd.reset(now);
			basic.reset(now);
			arcane.reset(now);
			nuke.reset(now);
			timer.reset(now);
			setDamage(0);
		} else if (c == '1') {
			tryCast(now, basic, 10);
		} else if (c == '2') {
			tryCast(now, arcane, 20);
		} else if (c == '3') {
			tryCast(now, nuke, 40);
		} else if (c == '4') {
			tryCast(now, iWin , 100);
		} else {
			System.out.println("unknown key: " + c);
		}
	}

	private void setDamage(int i) {
		totalDamage = i;
		damageText.setText("Damage: " + totalDamage);
	}

	private void tryCast(long now, CooldownMeter2 meter, int damage) {
		if (timer.getCooldown(now) < timer.getMaxValue() && gcd.canCast(now) && meter.canCast(now)) {
			gcd.consumeCooldown(now);
			meter.consumeCooldown(now);
			setDamage(totalDamage + damage);
		}
	}
}
