package com.rainbowcard.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PowerTools {
	/**
	 * get random two color ball numbers
	 * 
	 * @return
	 */
	public static String getRandom2ColorBall() {
		
		StringBuilder res = new StringBuilder("");
        StringBuilder sb = null;
        StringBuilder sb2 = new StringBuilder();

		List<Integer> resArr = new ArrayList<Integer>();
		
		for (int i = 0; i < 6; i++) {
			// get random int 1~33
			int number = new Random().nextInt(33) + 1;
			
			while (resArr.contains(number)) {
				number = new Random().nextInt(33) + 1;
			}
            resArr.add(number);
			
		}
		
		Collections.sort(resArr);

        for (int i = 0;i<resArr.size();i++){
            sb = new StringBuilder();
            if(resArr.get(i) < 10){
                sb.append(0);
            }
            sb.append(resArr.get(i));
            res.append(sb.toString());
            res.append(",");
        }
        String tempStr = res.toString();
		tempStr = tempStr.replace(",", "  ");
        res = new StringBuilder(tempStr);
		res.append(",");
        int blueNumber = new Random().nextInt(16) + 1;
        if(blueNumber < 10){
            sb2.append(0);
        }
        sb2.append(blueNumber);
		res.append(sb2.toString());
		return res.toString();
	}
    public static String getSelect2ColorBall(List<Integer> redArr,List<Integer> blueArr) {
        StringBuilder res = new StringBuilder("");
        StringBuilder blue = new StringBuilder("");
        Collections.sort(redArr);
        Collections.sort(blueArr);
        StringBuilder sb = null;
        StringBuilder sb2 = null;
        for (int i = 0;i<redArr.size();i++){
            sb = new StringBuilder();
            if(redArr.get(i) < 10){
                sb.append(0);
            }
            sb.append(redArr.get(i));
            res.append(sb.toString());
            res.append(",");
        }
        String tempStr = res.toString();
        tempStr = tempStr.replace(",", "  ");
        res = new StringBuilder(tempStr);
        res.append(",");
        for (int i = 0;i<blueArr.size();i++){
            sb2 = new StringBuilder();
            if(blueArr.get(i) < 10){
                sb2.append(0);
            }
            sb2.append(blueArr.get(i));
            blue.append(sb2.toString());
            blue.append(",");
        }
        blue = new StringBuilder(blue.toString().replace(",", "  "));
        res.append(blue);


        return res.toString();
    }

}
