/*Name: Terrence Doyle
Name: Samuel Lin*/
public class Page {

	public int vpn;//instance variable to store virtual page number
	public byte byteArray[];//array of bytes
//*************************************************************************************************************************		
	public Page(int vpn, byte[] byteArray) {//constructor to create pages
		this.vpn = vpn;
		this.byteArray = byteArray;
	}
//*************************************************************************************************************************
	public int getVirtPageNum() {//returns virtual page number
		return vpn;
	}
//*************************************************************************************************************************
	public byte getData(int offset) throws ArrayIndexOutOfBoundsException {
		if(offset < 0 || offset >= byteArray.length){
			throw new ArrayIndexOutOfBoundsException("invalid offset");//must throw exception
		}
			return byteArray[offset];//if offset is valid exits out of method
	}
}
