package org.centerm.Tollway.helper;

public class RespCode {
    public static String ResponseMsgTMS(String response_code) {
        String szMSG = null;
        switch (response_code) {
            case "01":
                szMSG = "ข้อมูลมีความยาวไม่ถูกต้อง";
                break;
            case "02":
                szMSG = "ข้อมูลมีรูปแบบไม่ถูกต้อง";
                break;
            case "03":
                szMSG = "Terminal Version ไม่ถูกต้อง";
                break;
            case "04":
                szMSG = "Message Version ไม่ถูกต้อง";
                break;
            case "05":
                szMSG = "CRC (Check Sum) ไม่ถูกต้อง";
                break;
            case "06":
                szMSG = "Transaction Code ไม่ถูกต้อง";
                break;
            case "07":
                szMSG = "Terminal Certificate ไม่ถูกต้อง";
                break;
            case "08":
                szMSG = "ไม่พบ Terminal ID";
                break;
            case "11":
                szMSG = "ไม่พบหมายเลขบัตรนี้ในระบบ";
                break;
            case "12":
                szMSG = "ไม่พบรายการที่สอบถาม";
                break;
            case "13":
                szMSG = "ไม่พบรายการที่ส่งมายกเลิก";
                break;
            case "14":
                szMSG = "ใช้เกินจำนวนเงินที่กำหนดต่อประเภทบัตร";
                break;
            case "15":
                szMSG = "ใช้เกินจำนวนที่กำหนดต่อ Merchant";
                break;
            case "95":
                szMSG = "ข้อมูลยอดรวม EDC กับ TMS ไม่ตรงกัน";
                break;
            case "96":
                szMSG = "รหัส TOP หมดอายุ";
                break;
            case "97":
                szMSG = "ขอรหัส TOP ไม่สำเร็จเนื่องจากส่ง SMS ไม่สำเร็จ";
                break;
            case "98":
                szMSG = "ใช้เกินจำนวนเงินที่ฝาก";
                break;
            case "94":
                szMSG = "ไม่ได้เซ็ต KTB Corporate ID";
                break;
            case "21":
                szMSG = "TMS ประมวลผลนานจน Timeout";
                break;
            case "22":
                szMSG = "TMS ไม่สามารถรองรับการติดต่อจาก EDC";
                break;
            case "31":
                szMSG = "เกิดข้อผิดพลาดเกี่ยวกับฐานข้อมูล";
                break;
            case "32":
                szMSG = "ข้อผิดพลาดอื่น ๆ จาก EMCI";
                break;
            case "33":
                szMSG = "Batch no ไม่ถูกต้อง";
                break;
            case "34":
                szMSG = "Terminal ID ไม่ถูกต้อง";
                break;
            case "40":
                szMSG = "ข้อผิดพลาดอื่น ๆ จาก EMCI";
                break;
            case "41":
                szMSG = "EMCI ประมวลผลนาน";
                break;
            case "42":
                szMSG = "ปัญหา Key ของ KTB";
                break;
            case "43":
                szMSG = "ป้อนรหัสไม่ถูกต้อง";
                break;
            case "44":
                szMSG = "บัตรไม่มีในระบบ";
                break;
            case "45":
                szMSG = "ระบบปฏิเสธการทำรายการ";
                break;
            case "46":
                szMSG = "ป้อนรหัสผิด 3 ครั้ง";
                break;
            case "47":
                szMSG = "ระบบปฏิเสธการทำรายการ";
                break;
            case "48":
                szMSG = "บัตรหมดอายุ";
                break;
            case "49":
                szMSG = "บัตรถูกอายัติ";
                break;
            case "50":
                szMSG = "ระบบปฏิเสธการทำรายการ";
                break;
            case "51":
                szMSG = "เลขประจำตัวประชาชนไม่ถูกต้อง";
                break;
            case "52":
                szMSG = "ประเภทบัตรไม่ถูกต้อง";
                break;
            case "57":
                szMSG = "รายการไม่อนุมัติ (บัตร)";
                break;
            case "59":
                szMSG = "ยึดบัตร ติดต่ออนุมัติวงเงิน";
                break;
            case "61":
                szMSG = "รายการไม่อนุมัติ (บัตร)";
                break;
            case "69":
                szMSG = "Terminal ID ไม่ถูก";
                break;
            case "75":
                szMSG = "รหัสไม่ถูกต้องเกินกำหนด";
                break;
            default:
                szMSG = "ข้อมูลไม่ถูกต้อง";
                break;

        }
//        return szMSG;
        return response_code + " - " + szMSG;    // Paul_20181020 Display response code
    }

//SINN 20181113  display = response_code + "\n" + szMSG; enough .
    public static String ResponseMsgPOS(String response_code) {
        String szMSG = null;
        switch (response_code) {
            case "01":
                szMSG = "ติดต่ออนุมัติวงเงิน";
                break;
            case "02":
                szMSG = "ติดต่ออนุมัติวงเงิน";
                break;
            case "03":
                szMSG = "ติดต่ออนุมัติวงเงิน";
                break;
            case "04":
                szMSG = "ยึดบัตร-ติดต่ออนุมัติวงเงิน";
                break;
            case "05":
                szMSG = "รายการไม่อนุมัติ (บัตร)";
                break;
            case "12":
                szMSG = "ติดต่อบริการร้านค้า";
                break;
            case "07":
                szMSG = "ยึดบัตร-ติดต่ออนุมัติวงเงิน";
                break;
            case "13":
                szMSG = "ยอดเงินไม่ถูกต้อง";
                break;
            case "14":
                szMSG = "รายการไม่อนุมัติ (บัตร)";
                break;
            case "19":
                szMSG = "กรุณาทำรายการใหม่";
                break;
            case "25":
                szMSG = "รายการไม่อนุมัติ (บัตร)";
                break;
            case "30":
                szMSG = "ติดต่อ THAIVAN";
                break;
            case "41":
                szMSG = "ยึดบัตร-ติดต่ออนุมัติวงเงิน";
                break;
            case "43":
                szMSG = "ยึดบัตร-ติดต่ออนุมัติวงเงิน";
                break;
            case "51":
                szMSG = "รายการไม่อนุมัติ (บัตร)";
                break;
            case "54":
                szMSG = "บัตรหมดอายุ";
                break;
            case "55":
                szMSG = "ใส่รหัสไม่ถูกต้อง";
                break;
            case "57":
                szMSG = "รายการไม่อนุมัติ (บัตร)";
                break;
            case "58":
                szMSG = "รายการไม่อนุมัติ (บัตร)";
                break;
            case "59":
                szMSG = "ยึดบัตร ติดต่ออนุมัติวงเงิน";
                break;
            case "61":
                szMSG = "รายการไม่อนุมัติ (บัตร)";
                break;
            case "75":
                szMSG = "รหัสไม่ถูกต้องเกินกำหนด";
                break;
            case "76":
                szMSG = "รายการไม่อนุมัติ (บัตร)";
                break;
            case "77":
                szMSG = "ติดต่อ THAIVAN";
                break;
            case "78":
                szMSG = "ติดต่อ THAIVAN";
                break;
            case "80":
                szMSG = "ติดต่อ THAIVAN";
                break;
            case "89":
                szMSG = "ติดต่อ THAIVAN";
                break;
            case "91":
                szMSG = "ระบบขัดข้อง ติดต่อบริการร้านค้า";
                break;
            case "94":
                szMSG = "ติดต่อบริการร้านค้า";
                break;
            case "96":
                szMSG = "ระบบขัดข้อง ติดต่อบริการร้านค้า";
                break;

            default:
                szMSG = "ติดต่อ THAIVAN";
                break;

        }
        return response_code + " - " + szMSG;
    }
//
//    public static String ResponseMsgPOS(String response_code) {
//        String szMSG = null;
//        switch (response_code) {
//            case "01":
//                szMSG = "ติดต่ออนุมัติวงเงิน 01";
//                break;
//            case "02":
//                szMSG = "ติดต่ออนุมัติวงเงิน 02";
//                break;
//            case "03":
//                szMSG = "ติดต่ออนุมัติวงเงิน 03";
//                break;
//            case "04":
//                szMSG = "ยึดบัตร-ติดต่ออนุมัติวงเงิน 04";
//                break;
//            case "05":
//                szMSG = "รายการไม่อนุมัติ (บัตร) 05";
//                break;
//            case "12":
//                szMSG = "ติดต่อบริการร้านค้า 12";
//                break;
//            case "07":
//                szMSG = "ยึดบัตร-ติดต่ออนุมัติวงเงิน 07";
//                break;
//            case "13":
//                szMSG = "ยอดเงินไม่ถูกต้อง 13";
//                break;
//            case "14":
//                szMSG = "รายการไม่อนุมัติ (บัตร) 14";
//                break;
//            case "19":
//                szMSG = "กรุณาทำรายการใหม่ 19";
//                break;
//            case "25":
//                szMSG = "รายการไม่อนุมัติ (บัตร) 25";
//                break;
//            case "30":
//                szMSG = "ติดต่อ THAIVAN 30";
//                break;
//            case "41":
//                szMSG = "ยึดบัตร-ติดต่ออนุมัติวงเงิน  41";
//                break;
//            case "43":
//                szMSG = "ยึดบัตร-ติดต่ออนุมัติวงเงิน 43";
//                break;
//            case "51":
//                szMSG = "รายการไม่อนุมัติ (บัตร) 51";
//                break;
//            case "54":
//                szMSG = "บัตรหมดอายุ 54";
//                break;
//            case "55":
//                szMSG = "ใส่รหัสไม่ถูกต้อง 55";
//                break;
//            case "58":
//                szMSG = "รายการไม่อนุมัติ (บัตร) 58";
//                break;
//            case "76":
//                szMSG = "รายการไม่อนุมัติ (บัตร) 76";
//                break;
//            case "77":
//                szMSG = "ติดต่อ THAIVAN 77";
//                break;
//            case "78":
//                szMSG = "ติดต่อ THAIVAN 78";
//                break;
//            case "80":
//                szMSG = "ติดต่อ THAIVAN 80";
//                break;
//            case "89":
//                szMSG = "ติดต่อ THAIVAN 89";
//                break;
//            case "91":
//                szMSG = "ระบบขัดข้อง ติดต่อบริการร้านค้า 91";
//                break;
//            case "94":
//                szMSG = "ติดต่อบริการร้านค้า 94";
//                break;
//            case "96":
//                szMSG = "ระบบขัดข้อง ติดต่อบริการร้านค้า 96";
//                break;
//
//            default:
//                szMSG = "ติดต่อ THAIVAN";
//                break;
//
//        }
//        return response_code + "\n" + szMSG;
//    }


//    public static String ResponseMsgGHC(String response_code) {
//        String szMSG = null;
//        switch (response_code) {
//            case "00":
//                szMSG = "รายการสำเร็จ";
//                break;
//            case "01":
//                szMSG = "ข้อมูลมีความยาวไม่ถูกต้อง";
//                break;
//            case "02":
//                szMSG = "ข้อมูลมีรูปแบบไม่ถูกต้อง";
//                break;
//            case "03":
//                szMSG = "Term Ver ไม่ถูกต้อง";
//                break;
//            case "04":
//                szMSG = "Message Version ไม่ถูกต้อง";
//                break;
//            case "05":
//                szMSG = "Check Sum ไม่ถูกต้อง";
//                break;
//            case "06":
//                szMSG = "Transaction Code ไม่ถูกต้อง";
//                break;
//            case "07":
//                szMSG = "Terminal Certificate ไม่ถูกต้อง";
//                break;
//            case "08":
//                szMSG = "ไม่พบ Terminal ID";
//                break;
//            case "11":
//                szMSG = "ไม่พบสิทธิในระบบติดต่อ Call Center";
//                break;
//            case "12":
//                szMSG = "ไม่พบรายการ";
//                break;
//            case "13":
//                szMSG = "ไม่พบรายการยกเลิก";
//                break;
//            case "14":
//                szMSG = "ใช้เกินวงเงินสิทธิที่ได้รับ";
//                break;
//            case "15":
//                szMSG = "ใช้เกินจำนวนที่กำหนด";
//                break;
//            case "95":
//                szMSG = "ข้อมูลยอดรวมไม่ถูกต้อง";
//                break;
//            case "96":
//                szMSG = "รหัส TOP เกินระยะเวลา";
//                break;
//            case "97":
//                szMSG = "ขอรหัส TOP ไม่สำเร็จ";
//                break;
//            case "98":
//                szMSG = "ใช้เกินจำนวนเงินที่ฝาก";
//                break;
//            case "94":
//                szMSG = "ไม่มี Corporate ID";
//                break;
//            case "21":
//                szMSG = "ประมวลผลเกินระยะเวลา";
//                break;
//            case "22":
//                szMSG = "EDC ติดต่อ TMS จำนวนมาก";
//                break;
//            case "31":
//                szMSG = "ฐานข้อมูลผิดพลาด";
//                break;
//            case "32":
//                szMSG = "ไม่พบสิทธิ์";
//                break;
//            case "33":
//                szMSG = "Batch no ไม่ถูกต้อง";
//                break;
//            case "34":
//                szMSG = "Terminal ID ไม่ถูกต้อง";
//                break;
//            case "40":
//                szMSG = "WELFARE ERROR";
//                break;
//            case "41":
//                szMSG = "WELFARE TIMEOUT";
//                break;
//            case "42":
//                szMSG = "Master Key ผิดพลาด";
//                break;
//            case "43":
//                szMSG = "ป้อนรหัสไม่ถูกต้อง";
//                break;
//            case "44":
//                szMSG = "ไม่พบสิทธิ์";
//                break;
//            case "45":
//                szMSG = "ระบบปฏิเสธการทำรายการ";
//                break;
//            case "46":
//                szMSG = "ป้อนรหัสผิด 3 ครั้ง";
//                break;
//            case "47":
//                szMSG = "ไม่อนุญาติให้ทำรายการ";
//                break;
//            case "48":
//                szMSG = "บัตรหมดอายุ";
//                break;
//            case "49":
//                szMSG = "ระงับสิทธิ์";
//                break;
//            case "50":
//                szMSG = "ไม่อนุญาติให้ทำรายการ";
//                break;
//            case "51":
//                szMSG = "เลขประจำตัวประชาชนไม่ถูกต้อง";
//                break;
//            case "52":
//                szMSG = "ผู้มีสิทธิ์อายุมากกว่า 7 ปีกรุณาเลือกเมนูอื่น";
//                break;
//            case "69":
//                szMSG = "Terminal ID ไม่ถูกต้อง";
//                break;
//            default:
//                szMSG = "ติดต่อ THAIVAN";
//                break;
//        }
//        return response_code + "\n" + szMSG;
//    }


    public static String ResponseMsgRS232(String szErr) {
        String szMSG = null;
        switch (szErr) {
            case "00":
                szMSG = "APPROVED            ";
                break;
            case "01":
                szMSG = "MSG LEN ERR         ";
                break;
            case "02":
                szMSG = "FORMAT ERR          ";
                break;
            case "03":
                szMSG = "TER VER ERR         ";
                break;
            case "04":
                szMSG = "MSG VER ERR         ";
                break;
            case "05":
                szMSG = "MAC ERR             ";
                break;
            case "06":
                szMSG = "TX CODE ERR         ";
                break;
            case "07":
                szMSG = "TER CER ERR         ";
                break;
            case "08":
                szMSG = "TID NOT FOUND       ";
                break;
            case "11":
                szMSG = "CARD NOT FOUND      ";
                break;
            case "12":
                szMSG = "TX NOT FOUND        ";
                break;
            case "13":
                szMSG = "VOID NOT MATCH      ";
                break;
            case "14":
                szMSG = "EXCEED AMT          ";
                break;
            case "15":
                szMSG = "EXCEED USE          ";
                break;
            case "95":
                szMSG = "TXN CODE ERR        ";
                break;
            case "96":
                szMSG = "TOP TIMEOUT         ";
                break;
            case "97":
                szMSG = "TOP FAIL            ";
                break;
            case "98":
                szMSG = "EXCEED AMT DEPOSIT  ";
                break;
            case "21":
                szMSG = "SERV TIMEOUT        ";
                break;
            case "22":
                szMSG = "TOO MANY CONN       ";
                break;
            case "31":
                szMSG = "DATABASE ERR        ";
                break;
            case "32":
                szMSG = "EMCI ERR            ";
                break;
            case "33":
                szMSG = "INVALID BATCH       ";
                break;
            case "34":
                szMSG = "TID NOT FOUND       ";
                break;
            case "40":
                szMSG = "EMCI ERR            ";
                break;
            case "41":
                szMSG = "EMCI TIMEOUT        ";
                break;
            case "42":
                szMSG = "EMCI MALFUNC        ";
                break;
            case "43":
                szMSG = "INCORRECT PIN       ";
                break;
            case "44":
                szMSG = "INVALID CARD        ";
                break;
            case "45":
                szMSG = "DO NOT HONOR        ";
                break;
            case "46":
                szMSG = "PIN EXCEED          ";
                break;
            case "47":
                szMSG = "TXN NOT PERMIT      ";
                break;
            case "48":
                szMSG = "CARD EXPIRE         ";
                break;
            case "49":
                szMSG = "PICKUP CARD         ";
                break;
            case "50":
                szMSG = "INVALID TXN         ";
                break;
            case "51":
                szMSG = "INVALID TIN/PIN     ";
                break;
            case "52":
                szMSG = "INV CARD CATG       ";
                break;
            case "69":
                szMSG = "INVALID TID         ";
                break;
            case "ND":
                szMSG = "TXN CANCEL          ";
                break;
            case "EN":
                szMSG = "CONNECT FAILED      ";
                break;
            case "NA":
                szMSG = "NOT AVAILABLE       ";
                break;
            default:
                szMSG = "TXN CANCEL          ";
                break;

        }

        return szMSG;
    }

    public static String ResponseMsgGHC(String response_code) {//K.GAME 180920 New ResponseMsgGHC
        String szMSG = null;
        switch (response_code) {
            case "00":
                szMSG = "รายการสำเร็จ";
                break;
            case "01":
                szMSG = "ข้อมูลมีความยาวไม่ถูกต้อง";
                break;
            case "02":
                szMSG = "ข้อมูลมีรูปแบบไม่ถูกต้อง";
                break;
            case "03":
                szMSG = "Terminal Version ไม่ถูกต้อง";
                break;
            case "04":
                szMSG = "Message Version ไม่ถูกต้อง";
                break;
            case "05":
                szMSG = "Check Sum ไม่ถูกต้อง";
                break;
            case "06":
                szMSG = "Transaction Code ไม่ถูกต้อง";
                break;
            case "07":
                szMSG = "Terminal Certificate ไม่ถูกต้อง";
                break;
            case "08":
                szMSG = "ไม่พบ Terminal ID";
                break;
            case "11":
                szMSG = "ไม่พบสิทธิ์\n" +
                        "กรุณาติดต่อ Call Center \n" +
                        "กรมบัญชีกลาง 022706400";
                break;
            case "12":
                szMSG = "ไม่พบรายการ";
                break;
            case "13":
                szMSG = "ไม่พบรายการยกเลิก";
                break;
            case "14":
                szMSG = "ใช้เกินวงเงินสิทธิที่ได้รับ \n" +
                        "กรุณาติดต่อ Call Center \n" +
                        "กรมบัญชีกลาง  022706400";
                break;
            case "15":
                szMSG = "ใช้เกินจำนวนที่กำหนด \n" +
                        "กรุณาติดต่อ Call Center \n" +
                        "กรมบัญชีกลาง  022706400";
                break;
            case "95":
                szMSG = "ข้อมูลยอดรวมไม่ถูกต้อง";
                break;
            case "96":
                szMSG = "รหัส TOP เกินระยะเวลาที่กำหนด";
                break;
            case "97":
                szMSG = "ขอรหัส TOP ไม่สำเร็จ";
                break;
            case "98":
                szMSG = "ใช้เกินจำนวนเงินที่ฝาก";
                break;
            case "94":
                szMSG = "ไม่มี Corporate ID";
                break;
            case "21":
                szMSG = "ประมวลผลเกินระยะเวลา";
                break;
            case "22":
                szMSG = "EDC ติดต่อ TMS จำนวนมาก";
                break;
            case "31":
                szMSG = "ฐานข้อมูลผิดพลาด";
                break;
            case "32":
                szMSG = "สิทธิของท่านถูกยกเลิก \n" +
                        "กรุณาติดต่อ Call Center \n" +
                        "กรมบัญชีกลาง  022706400";
                break;
            case "33":
                szMSG = "Batch no ไม่ถูกต้อง";
                break;
            case "34":
                szMSG = "Terminal ID ไม่ถูกต้อง";
                break;
            case "40":
                szMSG = "Payment ERROR";
                break;
            case "41":
                szMSG = "TIMEOUT";
                break;
            case "42":
                szMSG = "Master Key ผิดพลาด";
                break;
            case "43":
                szMSG = "รหัสผ่านไม่ถูกต้อง \n" +
                        "กรุณาใส่รหัสผ่านอีกครั้ง";
                break;
            case "44":
                szMSG = "ไม่พบสิทธิ์\n" +
                        "กรุณาติดต่อ Call Center \n" +
                        "กรมบัญชีกลาง 022706400";
                break;
            case "45":
                szMSG = "ระบบปฏิเสธการทำรายการ";
                break;
            case "46":
                szMSG = "ป้อนรหัสผิด 3 ครั้ง";
                break;
            case "47":
                szMSG = "ไม่อนุญาติให้ทำรายการ";
                break;
            case "48":
                szMSG = "บัตรหมดอายุ \n" +
                        "กรุณาติดต่อ Call Center \n" +
                        "กรมบัญชีกลาง 022706400";
                break;
            case "49":
                szMSG = "ระงับสิทธิ\n" +
                        "กรุณาติดต่อ Call Center \n" +
                        "กรมบัญชีกลาง 022706400";
                break;
            case "50":
                szMSG = "ไม่อนุญาติให้ทำรายการ";
                break;
            case "51":
                szMSG = "ไม่พบสิทธิ์\n" +
                        "กรุณาติดต่อ Call Center \n" +
                        "กรมบัญชีกลาง 022706400";
                break;
            case "52":
                szMSG = "ผู้ใช้สิทธิ์อายุมากกว่า 7 ปี\n" +
                        "กรุณาเลือกเมนูอื่น";
                break;
            case "69":
                szMSG = "ผู้ใช้สิทธิ์อายุระหว่าง 0-7 ปี\n" +
                        "กรุณาเลือกเมนูอื่น";
                break;
            default:
                szMSG = "ติดต่อ THAIVAN";
                break;
        }
        return response_code + " - " + szMSG;
    }
}
