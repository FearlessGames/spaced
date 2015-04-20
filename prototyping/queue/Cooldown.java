import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class Cooldown extends KeyAdapter implements Runnable {

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
	private JLabel queueText;
	private CooldownMeter2 timer;
	private char c0;
	

	Cooldown() {
		JFrame frame = new JFrame("Cooldown");

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
		timer = createMeter(frame, "timer", 60*1000, 0, null);


		//JButton start = new JButton("start");
		//frame.add(start);

		gcd = createMeter(frame, "gcd", 1000, 0, null);
		basic = createMeter(frame, "basic: 1", 1000, 0, gcd);
		arcane = createMeter(frame, "arcane: 2", 5000, 0, gcd);
		nuke = createMeter(frame, "nuke: 3", 6000, 0, gcd);
		iWin = createMeter(frame, "I win: 4", 30000, 0, gcd);
		//start.addKeyListener(this);
		frame.addKeyListener(this);

		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
		c0 = 0;
		new Thread(this).run();
	}

	public void run() {
		while( true ) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			
			long now = System.currentTimeMillis();
			
			if( c0 != 0 )
			{
				if( c0 == 'r' )
				{
					gcd.reset(now);
					basic.reset(now);
					arcane.reset(now);
					nuke.reset(now);
					iWin.reset(now);
					timer.reset(now);
					setDamage(0);
					c0 = 0;
				}
				else
				{
					if( cast( c0, now ) )
					{
						c0 = 0;
					}
				}
			}
			
			queueText.setText( "Queue: " + c0 );
		}		
	}
	

	private boolean cast( char c,  long now ) {
		if (c == '1') {
			return tryCast(now, basic, 10);
		} else if (c == '2') {
			return tryCast(now, arcane, 20);
		} else if (c == '3') {
			return tryCast(now, nuke, 40);
		} else if (c == '4') {
			return tryCast(now, iWin , 100);
		} else {
			System.out.println("unknown key: " + c);
		}
		
		return false;
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
		basic.queued = false;
		arcane.queued = false;
		nuke.queued = false;
		iWin.queued = false;

		c0 = arg0.getKeyChar();

		if (c0 == '1') basic.queued = true;
		if (c0 == '2') arcane.queued = true;
		if (c0 == '3') nuke.queued = true;
		if (c0 == '4') iWin.queued = true;
	}

	private void setDamage(int i) {
		totalDamage = i;
		String peenLength = "";
		for( int it=0; it<(int)(totalDamage / 40); ++it )
			peenLength = peenLength + "="; 
		damageText.setText("Damage: " + totalDamage + " peen 8" + peenLength + "D");
	}

	private boolean tryCast(long now, CooldownMeter2 meter, int damage) {
		if (timer.getCooldown(now) < timer.getMaxValue() && gcd.canCast(now) && meter.canCast(now)) {
			gcd.consumeCooldown(now);
			meter.consumeCooldown(now);
			setDamage(totalDamage + damage);
			meter.queued = false;
			return true;
		}
		
		return false;
	}
}
