package org.centerm.land.helper;

public class RespCode {
    public static String ResponseMsgTMS(String response_code)
    {      String szMSG = null;
        switch (response_code) {
            case "01":   szMSG="ข้อมูลมีความยาวไม่ถูกต้อง";
                break;
            case "02":   szMSG="ข้อมูลมีรูปแบบไม่ถูกต้อง";
                break;
            case "03":   szMSG="Terminal Version ไม่ถูกต้อง";
                break;
            case "04":   szMSG="Message Version ไม่ถูกต้อง";
                break;
            case "05":   szMSG="CRC (Check Sum) ไม่ถูกต้อง";
                break;
            case "06":   szMSG="Transaction Code ไม่ถูกต้อง";
                break;
            case "07":   szMSG="Terminal Certificate ไม่ถูกต้อง";
                break;
            case "08":   szMSG="ไม่พบ Terminal ID";
                break;
            case "11":   szMSG="ไม่พบหมายเลขบัตรนี้ในระบบ";
                break;
            case "12":   szMSG="ไม่พบรายการที่สอบถาม";
                break;
            case "13":   szMSG="ไม่พบรายการที่ส่งมายกเลิก";
                break;
            case "14":   szMSG="ใช้เกินจำนวนเงินที่กำหนดต่อประเภทบัตร";
                break;
            case "15":   szMSG="ใช้เกินจำนวนที่กำหนดต่อ Merchant";
                break;
            case "95":   szMSG="ข้อมูลยอดรวม EDC กับ TMS ไม่ตรงกัน";
                break;
            case "96":   szMSG="รหัส TOP หมดอายุ";
                break;
            case "97":   szMSG="ขอรหัส TOP ไม่สำเร็จเนื่องจากส่ง SMS ไม่สำเร็จ";
                break;
            case "98":   szMSG="ใช้เกินจำนวนเงินที่ฝาก";
                break;
            case "94":   szMSG="ไม่ได้เซ็ต KTB Corporate ID";
                break;
            case "21":   szMSG="TMS ประมวลผลนานจน Timeout";
                break;
            case "22":   szMSG="TMS ไม่สามารถรองรับการติดต่อจาก EDC";
                break;
            case "31":   szMSG="เกิดข้อผิดพลาดเกี่ยวกับฐานข้อมูล";
                break;
            case "32":   szMSG="ข้อผิดพลาดอื่น ๆ จาก EMCI";
                break;
            case "33":   szMSG="Batch no ไม่ถูกต้อง";
                break;
            case "34":   szMSG="Terminal ID ไม่ถูกต้อง";
                break;
            case "40":   szMSG="ข้อผิดพลาดอื่น ๆ จาก EMCI";
                break;
            case "41":   szMSG="EMCI ประมวลผลนาน";
                break;
            case "42":   szMSG="ปัญหา Key ของ KTB";
                break;
            case "43":   szMSG="ป้อนรหัสไม่ถูกต้อง";
                break;
            case "44":   szMSG="บัตรไม่มีในระบบ";
                break;
            case "45":   szMSG="ระบบปฏิเสธการทำรายการ";
                break;
            case "46":   szMSG="ป้อนรหัสผิด 3 ครั้ง";
                break;
            case "47":   szMSG="ระบบปฏิเสธการทำรายการ";
                break;
            case "48":   szMSG="บัตรหมดอายุ";
                break;
            case "49":   szMSG="บัตรถูกอายัติ";
                break;
            case "50":   szMSG="ระบบปฏิเสธการทำรายการ";
                break;
            case "51":   szMSG="เลขประจำตัวประชาชนไม่ถูกต้อง";
                break;
            case "52":   szMSG="ประเภทบัตรไม่ถูกต้อง";
                break;
            case "69":   szMSG="Terminal ID ไม่ถูก";
                break;
            default:
                szMSG="ข้อมูลไม่ถูกต้อง";
                break;

        }
        return  szMSG;
    }


    public static String ResponseMsgPOS(String response_code)
    {      String szMSG = null;
        switch (response_code) {
            case "01":   szMSG="ติดต่ออนุมัติวงเงิน 01";
                break;
            case "02":   szMSG="ติดต่ออนุมัติวงเงิน 02";
                break;
            case "03":   szMSG="ติดต่ออนุมัติวงเงิน 03";
                break;
            case "04":   szMSG="ยึดบัตร-ติดต่ออนุมัติวงเงิน 04";
                break;
            case "05":   szMSG="รายการไม่อนุมัติ (บัตร) 05";
                break;
            case "12":   szMSG="ติดต่อบริการร้านค้า 12";
                break;
            case "07":   szMSG="ยึดบัตร-ติดต่ออนุมัติวงเงิน 07";
                break;
            case "13":   szMSG="ยอดเงินไม่ถูกต้อง 13";
                break;
            case "14":   szMSG="รายการไม่อนุมัติ (บัตร) 14";
                break;
            case "19":   szMSG="กรุณาทำรายการใหม่ 19";
                break;
            case "25":   szMSG="รายการไม่อนุมัติ (บัตร) 25";
                break;
            case "30":   szMSG="ติดต่อ THAIVAN 30";
                break;
            case "41":   szMSG="ยึดบัตร-ติดต่ออนุมัติวงเงิน  41";
                break;
            case "43":   szMSG="ยึดบัตร-ติดต่ออนุมัติวงเงิน 43";
                break;
            case "51":   szMSG="รายการไม่อนุมัติ (บัตร) 51";
                break;
            case "54":   szMSG="บัตรหมดอายุ 54";
                break;
            case "55":   szMSG="ใส่รหัสไม่ถูกต้อง 55";
                break;
            case "58":   szMSG="รายการไม่อนุมัติ (บัตร) 58";
                break;
            case "76":   szMSG="รายการไม่อนุมัติ (บัตร) 76";
                break;
            case "77":   szMSG="ติดต่อ THAIVAN 77";
                break;
            case "78":   szMSG="ติดต่อ THAIVAN 78";
                break;
            case "80":   szMSG="ติดต่อ THAIVAN 80";
                break;
            case "89":   szMSG="ติดต่อ THAIVAN 89";
                break;
            case "91":   szMSG="ระบบขัดข้อง ติดต่อบริการร้านค้า 91";
                break;
            case "94":   szMSG="ติดต่อบริการร้านค้า 94";
                break;
            case "96":   szMSG="ระบบขัดข้อง ติดต่อบริการร้านค้า 96";
                break;

            default:
                szMSG="ติดต่อ THAIVAN";
                break;

        }
        return  szMSG;
    }


}
