package uml;

import java.util.ArrayList;
import java.util.HashSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Main {
	private static HashSet<String> useCaseIds = new HashSet<String>();
	private static HashSet<String> classIds = new HashSet<String>();
	private static ArrayList<Element> useCaseElements = new ArrayList<Element>();
	private static ArrayList<Element> classElements = new ArrayList<Element>();
	private static ArrayList<Element> generalizationElements = new ArrayList<Element>();
	private static ArrayList<Element> stereotypeElements = new ArrayList<Element>();
	public static void main(String args[]) throws DocumentException {
		
		SAXReader reader=new SAXReader();
		//EA导出的XML文件名
	    Document dom = reader.read("Untitled.xml");
	    Element root = dom.getRootElement();
	    
	    //获取UseCase和Class的包元素
	    ArrayList<Element> pakagedElementList = new ArrayList<Element>();
	    pakagedElementList.addAll(root.element("XMI.content").element("Model").element("Namespace.ownedElement").elements("Package"));
	    
	    
	    //遍历包
	    for(Element pakage : pakagedElementList) {
	    	
	    	//获取包中所有的useCase
	    	useCaseElements.addAll(pakage.element("Namespace.ownedElement").elements("UseCase"));
	    	//获取包中所有的class
	    	classElements.addAll(pakage.element("Namespace.ownedElement").elements("Class"));
	    	//获取包中所有的generalization关系
	    	generalizationElements.addAll(pakage.element("Namespace.ownedElement").elements("Generalization"));
	    	//获取包中所有的Stereotype
	    	stereotypeElements.addAll(pakage.element("Namespace.ownedElement").elements("Stereotype"));
	    	
	    	if (useCaseElements.size() != 0) {
	    		addUseCaseId();
				removeNoNeededUseCaseId();
			} else if (classElements.size() != 0) {
				addClassId();
				removeNoNeededClassId();
			}
	    	useCaseElements.clear();
	    	classElements.clear();
	    	generalizationElements.clear();
	    }
	    
	    System.out.println("useCseCount:" + useCaseIds.size() + "\n" + " classCount:" + classIds.size());
	    System.out.println("***RESULT*** " + (useCaseIds.size() * 4.6 + classIds.size() * 7.0));
	}

	private static void addUseCaseId() {
		//把所有的useCase的ID放入useCaseIds中
    	for(Element useCaseE : useCaseElements) {
    		String id = useCaseE.attributeValue("xmi.id");
    		useCaseIds.add(id);
    	}
	}
	
	private static void addClassId() {
		//把Entity XUGDG 类型的id放入rightTypeIds
		HashSet<String> rightTypeIds = new HashSet<String>();
		for(Element stereotypeE :stereotypeElements) {
			if (stereotypeE.attributeValue("name").equals("Entity") || stereotypeE.attributeValue("name").equals("XUGDG")) {
				String extendedElement = stereotypeE.attributeValue("extendedElement");
				String[] strings = extendedElement.split(" ");
				for (int i = 0; i < strings.length; i++) {
					rightTypeIds.add(strings[i]);
				}
			}
		}
		//把所有的class的ID放入classIds中
    	for(Element classE : classElements) {
    		String id = classE.attributeValue("xmi.id");
    		if(rightTypeIds.contains(id)) 
    			classIds.add(id);
    	}
	}
	
	private static void removeNoNeededUseCaseId() {
    	//去掉泛化父节点id
    	for(Element generalizationE : generalizationElements) {
    		String removeId = generalizationE.attributeValue("parent");
    		useCaseIds.remove(removeId);
    	}
	}
	
	private static void removeNoNeededClassId() {
    	//去掉泛化子节点id
    	for(Element generalizationE : generalizationElements) {
    		String removeId = generalizationE.attributeValue("child");
    		classIds.remove(removeId);
    		
    		//*由于图中没有标记父节点为Entity XUGDG 这里进行补充
    		String parentId = generalizationE.attributeValue("parent");
    		classIds.add(parentId);
    	}
	}
}
