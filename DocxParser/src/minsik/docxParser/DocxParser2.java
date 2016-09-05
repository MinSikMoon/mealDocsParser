package minsik.docxParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class DocxParser2 {

	public static String readDocxFile(String fileName) {

		try {
			FileInputStream fs = new FileInputStream(new File(fileName));
			OPCPackage d = OPCPackage.open(fs);
			if(fileName.endsWith(".docx")){
				XWPFWordExtractor xw = new XWPFWordExtractor(d);
				return xw.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args){
		String filePath = "D:\\javaide_new\\workspace\\DocxParser\\9.docx";
		//이제 docx 읽을 수 있으니까 날짜들을 넣을 수 있나 해본다. 
		//System.out.println(DocxParser.readDocxFile(filePath));
		String readedDocx = DocxParser2.readDocxFile(filePath);
		List<String> stringArr = new ArrayList<String>(); //단어들을 담는 배열
		//System.out.println(readedDocx);
		Scanner scanner = new Scanner(readedDocx);
		//읽어왔다. readedDocx를 한줄씩 읽어오고 싶다. 
		//문자열을 한줄 씩 읽는다. 
		//파싱하는데 필요한 재료들-------------------------------
		int start;
		int end;
		int confirmed = 0;
		String[] weekMealArr = new String[5]; //한주에 5일동안 중,석식이 나오니까.
		boolean blankFlag = false;
		char tempChar;
		String tempString = "";
		boolean isDateLine = false;
		//-------------------------------------------------
		List<String> tempStringList; //임시적으로 분리된 단어들이 들어간다. 

		List<String> dateList = new ArrayList(); //날짜들이 분리되어 들어간다.  

		List<String> menuList = new ArrayList();

		List<String> garbageList = new ArrayList();


		while(scanner.hasNextLine()){
			String line = scanner.nextLine(); 

			if(line.length() == 0){
				continue; //공백으로 시작하면 넘어간다.
			}
			//System.out.println("들어온 라인: " + line + ", 길이 : " + line.length());

			//들어온 라인을 단어별로 잘라주는 곳----------------------------------------------------
			if(line.contains("/")){ //날짜라인이라면 stringList가 아니라 dateList가 stringList가  된다. 날짜 리스트에 넣어준다. 
				tempStringList = dateList;
			}else if(line.contains("김치")){
				tempStringList = menuList; //김치라는 단어가 들어가면 menu다. 
			}else{
				tempStringList = garbageList; //날짜도 아니고 메뉴도 아닌 라인은 garbage 처리한다. 
			}

			for(int i=0; i<line.length(); i++){ //한글자씩 다 체크한다.
				if((tempChar = line.charAt(i)) == ' ' ||(tempChar = line.charAt(i)) == '\t'){ //일단 공백이 나타나면
					if(blankFlag == true){ //즉 방금 전까지 공백이 아니었다면 
						//넣어주고 초기화 //근데 이게 '구분'이라는 것이면 그냥 넘어간다. 
						if(tempString.contains("구분")){
							tempString = "";
							blankFlag = false; //flag를 false로 내려준다.
							continue;
						}
						tempStringList.add(tempString);
						tempString = "";
						blankFlag = false; //flag를 false로 내려준다.
					}else{ //그게 아니고 flag가 false, 즉 방금 전에도 공백이었다면
						continue; //계속해 계속
					}
				}else{ //공백이 아니라면 
					blankFlag = true; //공백이 아니면 일단 flag는 true다.
					tempString += tempChar; //temp에 방금 구한 char를 붙여준다. 
					if(i == (line.length()-1)){
						tempStringList.add(tempString);
						tempString = "";
						//System.out.println("방금 " + tempString + "도 넣었지롱 ㅎㅎ");
					}
				}
			}
			//---------------------------------------------------------------------------
		}
		String[] dateArr = new String[dateList.size()/2];
		String[] menuArr = new String[dateList.size()]; //date의 2배니까
		Arrays.fill(menuArr, "");
		
		for(int i=0; i< dateArr.length; i++){
			dateArr[i] = (dateList.get(i*2) + dateList.get(i*2+1));
		}
		

		
		String tempMenuString = "";
		boolean riceFlag = false;
		int menuCnt = -1;
		//이제 menuList를 돌면서 menuArr에 넣어주자.
		//------------------------------------메뉴리스트 파싱 시작----------------------
		Iterator<String> itr = menuList.iterator();
		
		for(int i=0; i< menuList.size(); i++){
			if(i == (menuList.size()-1)){
				//마지막 이라면 현재 임시문자열을 넣어줘야지
				menuArr[menuCnt] = tempMenuString;
			}
				
			//하나의 메뉴가 들어온다. 
			//공휴일이라면 
			System.out.println("====지금 " + i + "번째 룹에서 걸린 menuList의 원소는 => " + menuList.get(i));
			if(menuList.get(i).contains("석식") || menuList.get(i).contains("중식") || 
					menuList.get(i).length() == 0){
				System.out.println("얘는 나가리입니다.");
				continue;
			}
				
			
			if(menuList.get(i).contains("공휴일")){
				System.out.println("==공휴일 걸림==");
				//넣기전에 임시문자열을 넣어줘야지.
				menuArr[menuCnt] = tempMenuString;
				tempMenuString = "";
				menuCnt++;
				System.out.println("공휴일을 " + menuCnt + "와 " + (menuCnt + 5) + "에 넣습니다.. ");
				menuArr[menuCnt] = "공휴일";
				menuArr[menuCnt+5] = "공휴일";
				
				//menuCnt++; 
				continue;
			}
			if(menuList.get(i).endsWith("밥") || menuList.get(i).endsWith("라이스") || 
					menuList.get(i).endsWith("죽")){
				//이전에 모아 두었던 tempMenuString을 arr에 넣어준다./ 다만 이게 공백이라면 안 넣어준다.
				System.out.println(menuList.get(i) + "/이 걸렸습니다. 아직 넣지 않은 임시메뉴스트링은 : " + tempMenuString + " /현재 메뉴카운트" + menuCnt);
				if(!tempMenuString.equals("")){
					if(menuArr[menuCnt].equals("공휴일")){
						
						menuCnt++;
						System.out.println("공휴일이 들어있어 cnt + 1 해줬습니다. 현재 카운트 => " + menuCnt );
					}
					menuArr[menuCnt] = tempMenuString;
					System.out.println("방금 넣은 menuSTRING:" + tempMenuString + " in " + menuCnt);
					tempMenuString = "";
				}
				menuCnt++;
				tempMenuString += menuList.get(i);
			}else{ //밥,죽, 라이스가 아니라면
				tempMenuString += (", " +menuList.get(i));
				System.out.println(menuList.get(i) + "를 붙여줌. 현재 임시 메뉴문자열은  => " + tempMenuString);
			}
		}
//---------------------------------------------------------------------------
//		for(String str : dateArr){
//			System.out.println(str);
//		}
//
		for(String str : menuList){
//			if(str.length() == 0)
//				continue;
			System.out.println(str);
		}
//		System.out.println(dateArr.length + "/" + menuArr.length);
		//System.out.println("첫번째 크기" + menuArr[0].length());
		
		//이제 dateArr, menuArr이 다 갖춰졌다. 
		
		for(int i=0; i< dateArr.length; i++){
			int groupIdx = i/5;
			System.out.print(dateArr[i] + " 중식 : ");
			System.out.println(menuArr[i+(groupIdx*5)]);
			System.out.print(dateArr[i] + "석식 :");
			System.out.println(menuArr[i+((groupIdx+1)*5)]);
		}

	}
}
