import java.util.*;
import java.io.*;
import java.math.BigInteger;
public class IBANvalidator{
	private Map<String, Integer> countryIBANLength;
	private String alphabet;
	private final int alphabetIBANexpand = 10;
	public IBANvalidator(){
		this.alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		this.countryIBANLength = new HashMap<String, Integer>();
		try{
			File file = new File("countryIBAN");
			Scanner sc = new Scanner(file); 
			while(sc.hasNextLine()){
				String[] splitted = sc.nextLine().split("\\s+");
				this.countryIBANLength.put(splitted[0],Integer.parseInt(splitted[1]));
			} 
		}catch(FileNotFoundException e){
			System.out.println("File 'countyIBAN' not found");
		}
		
	}

	public boolean checkIbanLength(String iban){
		try{
			if(this.countryIBANLength.get(iban.substring(0,2)) == iban.length()){
				return true;
			}else{
				return false;
			}
			
		}catch(NullPointerException e){
			System.out.println(iban.substring(0,2)
								 + " not found in 'countryIBAN' file");
			return false;
		}
	}

	public String moveFourInitCharToEnd(String iban){
		return iban.substring(4,iban.length()) + iban.substring(0,4);
	}

	public String convertIbanToNumbers(String iban){
		String numericalIBAN = "";
		boolean foundChar = false;
		for(int i = 0; i < iban.length(); i++){
			for(int j = 0; j < this.alphabet.length(); j++){
				if(iban.substring(i,i+1).equals(this.alphabet.substring(j,j+1))){
					numericalIBAN += j + this.alphabetIBANexpand; 
					foundChar = true;
					break;
				}
			}
			if(foundChar){
				foundChar = false;
			}else{
				numericalIBAN += iban.substring(i,i+1);
			}
		}
		return numericalIBAN;
	}

	public int ibanDivisionBy97(String iban){
		BigInteger numIBAN = new BigInteger(iban);
		BigInteger divider = new BigInteger("97");
		return  numIBAN.remainder(divider).intValue();
	}

	public boolean checkIBANvalid(String iban){
		if(this.checkIbanLength(iban)){
			String numericalIBAN = this.convertIbanToNumbers(
										this.moveFourInitCharToEnd(iban)
									);
			if(this.ibanDivisionBy97(numericalIBAN) == 1){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	public static void main(String [] args){
		IBANvalidator validator = new IBANvalidator();
		if(args.length > 0){
			try{
				for( int i = 0; i < args.length; i++){
					File fileRead = new File(args[i]);
					Scanner sc = new Scanner(fileRead);
					PrintWriter writer = new PrintWriter("output/"+args[i]+".out", "UTF-8");
					while(sc.hasNextLine()){
						String curLine = sc.nextLine();
						if(validator.checkIBANvalid(curLine)){
							writer.println(curLine + ";true");
						}else{
							writer.println(curLine + ";false");
						}
					} 
					writer.close();
				}
			}catch(FileNotFoundException e){
				System.out.println("Specified file not found!");
			}catch(IOException e){
				System.out.println("Writing file error");
			}
		}else{
			System.out.println("Provide IBAN for validation:");
			Scanner sc = new Scanner(System.in);
			System.out.println(validator.checkIBANvalid(sc.nextLine()));
		}
	}
}