
public class Banker {
	private int nUnits;
	
	public Banker(int nUnits) {
		this.nUnits = nUnits;
	}
	
	public void setClaim(int nUnits) {
		
	}
	
	public boolean request(int nUnits) {
		return false;
	}
	
	public void release(int nUnits) {
		
	}
	
	public int allocated() {
		return 0;
	}
	
	public int remaining() {
		return 0;
	}
}
