package com.jian.nettyclient.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class XMLUtil {

    private static Logger logger = LoggerFactory.getLogger(XMLUtil.class);

    public static Document getDocument(Object b) throws Exception{
        Document document = DocumentHelper.createDocument();
        try {
            Element root = document.addElement(b.getClass().getSimpleName());
            Field[] field = b.getClass().getDeclaredFields(); // 获取实体类b的所有属性，返回Field数组
            for (Field value : field) { // 遍历所有有属性
                String name = value.getName(); // 获取属属性的名字
                if (!name.equals("serialVersionUID")) {//去除串行化序列属性
                    name = name.substring(0, 1).toUpperCase()
                            + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                    Method m = b.getClass().getMethod("get" + name);
                    String propertievalue = (String) m.invoke(b);// 获取属性值
                    Element propertie = root.addElement(name);
                    propertie.setText(propertievalue);
                }
            }
        } catch (Exception e) {
            logger.error("parse error",e);
            throw e;
        }
        return document;
    }

    public static String getXmlString(Object b) throws Exception {
        return getDocument(b).asXML();
    }

    public static String getReq(){
        return "<?xml version=\"1.0\" encoding=\"utf-8\" ?><root><N5Rt>320095130</N5Rt><WmvYpZ>79033028</WmvYpZ><REQ>999999999</REQ><fbmu>HdxS1JCNmz</fbmu><QmtYgB6z>-1152973356.8482318</QmtYgB6z><bCdGwBu2>268188333</bCdGwBu2><u-0TjDc>1876007775</u-0TjDc><E><HJi4-E>443827839.0678897</HJi4-E><_4CF2AJDW>623103414</_4CF2AJDW><HTwzerj>ZlSx</HTwzerj><m5bre3><an2MJUA6Z2>-2090261948</an2MJUA6Z2><_GUW></_GUW><kzbHph0i> h</kzbHph0i><j></j><q_SUGa>AIG1gK1tds</q_SUGa><CRI9z></CRI9z><n>1722705285.2641463</n><gpUglnS5>1091230322</gpUglnS5></m5bre3><mUe58EKj><w3Zxpbg3N>-445850595.81707525</w3Zxpbg3N><B></B><Wt></Wt><vOGs>P8</vOGs><l>-1499123417</l><O>33663637</O><c0Sc>fcZ5KGo</c0Sc><x>F3K_98A</x></mUe58EKj><V>260192667</V><oqrASF>j@@</oqrASF><r>-1480499754.4591885</r></E></root>";
    }

    public static String getResp(){
        return "<?xml version=\"1.0\" encoding=\"utf-8\" ?><root><xKzrD><RESP>888888888</RESP><D0ZRz5Xm>5CF</D0ZRz5Xm><HsxzG>2016304080.0980945</HsxzG><m>3ROdjb</m><t>-1656497225.634419</t><PB><MIM1WB>-48634281.066963196</MIM1WB><U>-11140784.507437706</U><yItJq>-1368165529</yItJq><wcp>5FZ9</wcp><Q0je5SYjU9>-36751973</Q0je5SYjU9><sjNU>-224050220</sjNU><zLzZ>1531693012.3479514</zLzZ><al5fO7zSi>b</al5fO7zSi></PB><P>WRl8rsza</P><ahHD>1977076675.2727675</ahHD></xKzrD><X0jK61>1981024959.9955983</X0jK61><XM>1579754307</XM><KQr2Ow>j0</KQr2Ow><AA>8FvJP </AA><fE8TeqwE>630080251</fE8TeqwE><GC>267158484</GC><Ev>v 704j</Ev></root>";
    }

}
