package minsik.docxParser;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class DocxParser {

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
		String filePath = "D:\\javaide_new\\workspace\\DocxParser\\8.docx";
		//이제 docx 읽을 수 있으니까 날짜들을 넣을 수 있나 해본다. 
		//System.out.println(DocxParser.readDocxFile(filePath));
		String readedDocx = DocxParser.readDocxFile(filePath);
		List<String> stringArr = new ArrayList<String>(); //단어들을 담는 배열
		System.out.println(readedDocx);

		//1. 날짜 배열에 넣기
		//먼저 하나의 엄청 긴 배열을 만든다. 공백을 만나면 다음 공백이 아닐때까지 
		String temp = "";
		char tempChar;
		boolean flag = false;


		for(int i=0; i<readedDocx.length(); i++){ //한글자씩 다 체크한다.
			if((tempChar = readedDocx.charAt(i)) == ' ' ||(tempChar = readedDocx.charAt(i)) == '\t'){ //일단 공백이 나타나면
				if(flag == true){ //즉 방금 전까지 공백이 아니었다면 
					//temp를 배열에 넣어주고 초기화
					stringArr.add(temp);
					temp = "";
					flag = false; //flag를 false로 내려준다.
				}else{ //그게 아니고 flag가 false, 즉 방금 전에도 공백이었다면
					continue; //계속해 계속
				}
			}else{ //공백이 아니라면 
				flag = true; //공백이 아니면 일단 flag는 true다.
				temp += tempChar; //temp에 방금 구한 char를 붙여준다. 
			}
		}

		//디버깅용 배열 출력해보기
		for(String str : stringArr){
			System.out.println(str);
		} // 모든 단어들을 분리 했다. 이제는 날짜만 넣어보자. 

		String dateTemp = "";
		boolean dateFlag = false; // 날짜용 플래그
		String tempDate;
		String realDate = "";
		List<String> dateArr = new ArrayList<String>(); //날짜와 요일들을 담는 배열

		for(int i=0; i<stringArr.size(); i++){ //단어 배열들을 각각 돈다. 
			// /슬래시를 포함하고 있으면 그것은 날짜라는 뜻이므로 슬래시를 만나면 날짜 배열에 넣어준다. 
			// 그리고 다음에 오는 요일도 같이 넣어준다.  
			tempDate = stringArr.get(i);
			//System.out.println(i);
			if(tempDate.contains("/")){
				//아직 넣지는 말고 realDate로 임시 보관해준다. 요일과 붙여서 넣어줄 예정
				realDate += tempDate;
				dateFlag = true;
				continue;
			}
			if(dateFlag){
				realDate += tempDate;
				//중식이라는 얘가 들어오면 강제로 길이를 잘라준다. 
				if(realDate.contains("중식")){
					realDate = realDate.substring(0, 7);
				}
				dateArr.add(realDate);
				realDate = "";
				dateFlag = false;
			}


		}

		for(String str : dateArr){
			System.out.println(str);
		}//---------------------------------날짜는 다 빼왔다. 
		//먼저 석식, 중식이 들어가는 해쉬맵 만든다. / 노드역할 해주는 얘임
		Map<String,String> twoMeals = new HashMap(); //석식과 메뉴가 담긴다. 
		Map<String, Map<String,String>> dateMeals = new HashMap(); //요일과 석식, 
		//일단 "밥", "라이스" 가 들어가면 식사의 시작, 다음 "밥" "라이스"가 나올때 까지가 한 단위이다. 
		//stringArr가 우리들의 소스이다. 여기에 모든 단어들이 다 들어가 있다. 
		//먼저 '밥' '라이스'가 들어간 얘의 갯수를 센다. 
		//먼저 날짜의 갯수를 구한다. 
		int dateNum = dateArr.size();
		System.out.println(dateNum + "개");
		//이제 밥, 라이스가 나온 단어의 갯수를 구해보자. 
		int mealNum = 0;
		List<String> mealArr = new ArrayList();
		String tempMeal;
		
		for(int i=0; i<stringArr.size(); i++){
			// 밥, 라이스 라는 단어를 포하하고 있다면  mealArr에 넣어준다. 
			// 그리고 다음에 오는 요일도 같이 넣어준다.  
			tempMeal = stringArr.get(i);
			//System.out.println(i);
			if(tempMeal.contains("김치") || tempMeal.contains("깍두기")){
				System.out.println(tempMeal);
				mealNum++;
			}
		}
		System.out.println("밥:" + mealNum );
		//		//테스트용 밥노드생성 / 날짜 하나당 맵핑 
		//		Map<String, String> tempMap = new HashMap<String,String>();
		//		tempMap.put("석식", "김치와 밥");
		//		tempMap.put("중식", "카레라이스와 김치, 된장국!");
		//		//석식, 중식 해쉬맵을 날짜랑 맵핑(날짜 해쉬맵)에 넣어준다. 
		//		dateMeals.put("8월1일", tempMap);
		//		System.out.println(dateMeals.get("8월1일").get("석식"));
		//		System.out.println(dateMeals.get("8월1일").get("중식"));


	}
}
