/*Name: Terrence Doyle
Name: Samuel Lin*/
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OS {
	public Page pageArray [];//array of page objects
	public String fileArray[];//page table file stored in an array line by line
	public int pageTableArray[];//array of vpn to its corresponding ppn
	public int numPages;//Number of pages (from page table file)
	public int numBytes;//number of bytes per page (from page table file)
	//public int tlb[][] = new int[3][3]; @@@@@@@@@@
//*************************************************************************************************************************	
	public OS(String filename) throws FileNotFoundException, IOException {//constructor, loads file for pageTable
		fileArray = fileToArray(filename);//converts the file to an array
		numPages = getNumPages(fileArray[0]);//stores the num of pages
		numBytes = getNumBytes(fileArray[0]);//stores bytes per page
		pageArray = new Page[numPages];//sets the size of page array to the number of pages present
		pageTableArray = new int[numPages];//sets the length of the page table to the number of pages
		pageTableFileToPageTableArray(fileArray,pageTableArray);//creates the Pagetable array for later easier access
		dataToPage(fileArray,pageArray);//stores the data to its respected page
	}
//*************************************************************************************************************************	
	public int getNumPages(String fileArray){
		return splitStringToIntArray(fileArray, " ")[0];//we are splitting with a space since they are sepparated with a space
	}
//*************************************************************************************************************************	
	public int getNumBytes(String fileArray){
		return splitStringToIntArray(fileArray, " ")[1];//splitting with a space
	}
//*************************************************************************************************************************													
	public int[] splitStringToIntArray(String fileArray, String stringToSplitWith){//this method takes in the first line from the fileArray splits the two numbers by the space then inserts them into an array, because the first number is always the pageNum we know that array[0] will be page num and array[1] will be bytes per page
		String[] strArray = fileArray.split(stringToSplitWith);//since the first line is split with a space and the second is split with -> we are using this parameter inorder for us to be able to reuse the function
		int[] intArray = new int[strArray.length];
		for(int i = 0; i < strArray.length; i++) {//length should always be 2
		    intArray[i] = Integer.parseInt(strArray[i]);//turns the string to an int
		}
		return intArray;
	}
//*************************************************************************************************************************	
	public void pageTableFileToPageTableArray(String[] fileArray, int[] pageTableArray){//converts the pagetable part of the fileArray to a just a page table, also splits it
		String[] pageTableBeforeSplitArray = new String[numPages];//creates a temp array storing the pagetable but before a split occurs
		int[] tempArray = new int[2];//since we are only spliting with one char the size will always be 2
		
		for (int k = 1; k<=numPages; k++){//starts at the second line and ends at the last line with page values
			pageTableBeforeSplitArray[k-1] = fileArray[k];//k-1 because the array is only as long as the numPages, and the loop starts at index 1 rather than 0
		}
		for (int k = 0; k<numPages; k++){//this loop takes the pageTableArray that hasnt been split yet (pageTableBeforeSplitArray) splits it and only stores the ppn in it, we use the index as the vpn
			tempArray = splitStringToIntArray(pageTableBeforeSplitArray[k], "->");//splits the array and stores it in temp, we do this because when splitStringtoIntArray returns a value it is always an array of size 2. because it runs in a loop the tempArray will constantly be over written
			pageTableArray[k] = tempArray[1];//the index 1 is the ppn address
		}
	}
//*****************************************ERROR IN THIS METHOD********************************************************************************	
	public void dataToPage(String fileArray[],Page pageArray []){//stores the data in the page
		for (int k = 0; k < numPages; k++){
			int lineNumber = numPages + k + 1;//gets the line number that the data is on
			byte[] byteArray = stringToByteArray(fileArray[lineNumber]);//converts data on line to a byte Array
			pageArray[getPPN(k)] = createPage(k,byteArray);//creates page finds vpn and stores both vpn and byteArray in it. Stores page in the PPN of k
		}//numPages + 1 = first index of data in fileArray[]
	}//data is being correctly stored in pages
//*************************************************************************************************************************	
	public byte[] stringToByteArray(String data){//this will take in a string from the fileArray(at one of its indexs) and put every character into its own index of a byte array
		 byte[] dataArray = new byte [data.length()];//creates an array which we will later return. since each index is represented by a byte we use the amount of chars in the string as the length
			for(int k = 0; k<data.length(); k++){//goes through each char in string converts it to a byte and stores it in the byte Array
				dataArray[k] = charToInt(data.charAt(k));
			}
			return dataArray;
	}
//*************************************************************************************************************************	
	public byte charToInt(char charValue){//takes in a char and gives its ascii byte value           	          
		byte byteValue = (byte) charValue; //casts the charValue as a byte    
		return byteValue;  
	}
//*************************************************************************************************************************	
	public Page createPage(int vpn, byte[] byteArray){//uses the constructor from the page Class to create a page object, page will be over written after it is stored in an array of Page objects
		Page page  = new Page(vpn, byteArray);
		return page;
	}
//*************************************************************************************************************************	
	public String[] fileToArray(String filename) throws IOException, FileNotFoundException {//turns the file into a string array
		BufferedReader in = new BufferedReader(new FileReader(filename));
        String str;

        List<String> list = new ArrayList<String>();//NOT MY METHOD
        while((str = in.readLine()) != null){
            list.add(str);
        }
        String[] fileArray = list.toArray(new String[0]);
        return fileArray;//stores each line into an index of an array       
	}
//*************************************************************************************************************************	
	public int getPPN(int vpn){//returns PPN given VPN format is vpn->ppn
		return pageTableArray[vpn];
	}
//*************************************************************************************************************************
	public int getVPN(int ppn){//returns the VPN given the PPN format is vpn->ppn
		for(int k = 0; k<numPages;k++){
			if(pageTableArray[k] == ppn)//this loops checks the index values rather than the values that are at the index
				return k;
		}
		System.out.println("invalid ppn");
		return -1;//-1 means error occured
	}
//*************************************************************************************************************************
	public Page getPage(int vpn){//gets a Page stored at a given vpn from the array of pages
		return pageArray[getPPN(vpn)];
	}
//*************************************************************************************************************************
	public byte getDataAtVirtAddress(int virtAddress){//virtAddress is the char number
		int virtAddressVPN = virtAddress / numBytes;//uses the property that when ints are divided remainder is cut off
		int virtAddressPPN = getPPN(virtAddressVPN);
		byte data = pageArray[getPPN(virtAddressPPN)].getData(virtAddress%numBytes);//gets page that the virtAddress is on from the page array then uses the getData method from the Page class to find the offset. virtAddressPPN * numBytes gets us to the line we need to be at and virtAddress%16 adds the remainder to it to get the data from that line
		return data;//virtAddressPPN * numBytes + virtAddress%16
	}
//*************************************************************************************************************************
	public static void main(String[] args) throws FileNotFoundException, IOException{
		OS os = new OS("proj2_data_large.txt");
		byte tlb[][] = new byte[50][50];
		if(isEmpty(tlb)){
			
		}
	}
	public static boolean isEmpty(byte tlb[][]){
		for(int i=0;i<50;i++){
			for(int k=0;k<50;k++){
				if(tlb[i][k] == 0){
					return true;
				}
			}
		}
		return false;
	}
}