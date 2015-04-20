import javax.swing.JProgressBar;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


public class JCooldown extends JProgressBar implements Runnable {

	public boolean queued = false; 
	public CooldownMeter2 meter;
	private CooldownMeter2 gcd;
	
	public JCooldown(CooldownMeter2 meter, CooldownMeter2 gcd) {
		super(0, 0, (int) meter.getCost());
		this.meter = meter;
		if (gcd == null) {
			gcd = meter;
		}
		this.gcd = gcd;
		new Thread(this).start();
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			repaint();
			/*
			long now = System.currentTimeMillis();
			int cooldown = (int) meter.getCooldown(now);
			int cost = (int) meter.getCost();
			long buffer = meter.getMaxValue() - meter.getCost();
			//System.out.println(cooldown);
			if (meter.canCast(now)) {
				setBackground(Color.green);
				setForeground(Color.blue);
				setValue((int) (cooldown - cost));
				setMaximum((int) (buffer));
			} else {
				setBackground(Color.white);
				setForeground(Color.red);
				setMaximum((int) meter.getCost());
				setValue(cooldown);
			}
			*/
		}
	}

	@Override
	public void paintComponent(Graphics g) {
	
		long now = System.currentTimeMillis();

		long cost = meter.getCost();
		long maxValue = meter.getMaxValue();
		long cooldown = meter.getCooldown(now);
		
		Rectangle bounds = g.getClipBounds();

		int width = (int) bounds.getWidth();
		int costPoint = (int) (width * cost / maxValue);
		int cooldownPoint = (int) (width * cooldown / maxValue);
		int lastConsumedPoint = (int) (width * meter.getLastConsumed() / maxValue);
		
		// background
		g.setColor(Color.WHITE);
		g.fillRect((int) bounds.getMinX(), (int) bounds.getMinY(), costPoint, (int) bounds.getHeight());

		g.setColor(Color.BLUE);
		g.fillRect((int) bounds.getMinX() + costPoint, (int) bounds.getMinY(), (int) bounds.getMaxX(), (int) bounds.getHeight());

		
		int arc = (int) (bounds.getHeight() / 3);
		int height2 = (int) (bounds.getHeight() * 0.8);
		int offset = (int) (bounds.getHeight() - height2) / 2;
		
		// foreground
		if (meter.canCast(now) && gcd.canCast(now)) {
			g.setColor(Color.GREEN);
		} else {
			g.setColor(Color.RED);
		}
		halfRoundRect(g, (int) bounds.getMinX(), (int) bounds.getMinY() + offset, cooldownPoint, height2, arc);
		
		g.setColor(Color.YELLOW);
		halfRoundRect(g, (int) bounds.getMinX(), (int) bounds.getMinY() + offset, lastConsumedPoint, height2, arc);
		
		if( meter.queued ) {
			g.setColor(Color.BLACK);
			g.drawRect((int) bounds.getMinX()+3, (int) bounds.getMinY()+3, width-7, (int) bounds.getHeight()-7);
			g.drawRect((int) bounds.getMinX()+2, (int) bounds.getMinY()+2, width-5, (int) bounds.getHeight()-5);
		}
	}

	private void halfRoundRect(Graphics g, int x, int y, int width, int height, int arc) {
		g.fillRoundRect(x, y, width, height, arc, arc);
		g.fillRect(x + width - arc, y, arc, height);
	}
}
