package Mars;

class Mothership extends Entity {
	public Mothership(Location location) {
		super(location);
	}

	public void emitSignal(Field f) {
		for (int row = 0; row < f.getDepth(); row++) {
			for (int col = 0; col < f.getWidth(); col++) {
				int d1 = Math.abs(row - this.getLocation().getRow());
				int d2 = f.getDepth() - d1;
				int x = Math.min(d1 * d1, d2 * d2);

				int d3 = Math.abs(col - this.getLocation().getCol());
				int d4 = f.getWidth() - d3;
				int y = Math.min(d3 * d3, d4 * d4);
				int signal = f.getDepth() * f.getDepth() + f.getWidth() * f.getWidth() - (x + y);
				f.setSignalStrength(row, col, signal);
			}
		}
	}
}
