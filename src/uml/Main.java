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
		//EA������XML�ļ���
	    Document dom = reader.read("Untitled.xml");
	    Element root = dom.getRootElement();
	    
	    //��ȡUseCase��Class�İ�Ԫ��
	    ArrayList<Element> pakagedElementList = new ArrayList<Element>();
	    pakagedElementList.addAll(root.element("XMI.content").element("Model").element("Namespace.ownedElement").elements("Package"));
	    
	    
	    //������
	    for(Element pakage : pakagedElementList) {
	    	
	    	//��ȡ�������е�useCase
	    	useCaseElements.addAll(pakage.element("Namespace.ownedElement").elements("UseCase"));
	    	//��ȡ�������е�class
	    	classElements.addAll(pakage.element("Namespace.ownedElement").elements("Class"));
	    	//��ȡ�������е�generalization��ϵ
	    	generalizationElements.addAll(pakage.element("Namespace.ownedElement").elements("Generalization"));
	    	//��ȡ�������е�Stereotype
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
		//�����е�useCase��ID����useCaseIds��
    	for(Element useCaseE : useCaseElements) {
    		String id = useCaseE.attributeValue("xmi.id");
    		useCaseIds.add(id);
    	}
	}
	
	private static void addClassId() {
		//��Entity XUGDG ���͵�id����rightTypeIds
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
		//�����е�class��ID����classIds��
    	for(Element classE : classElements) {
    		String id = classE.attributeValue("xmi.id");
    		if(rightTypeIds.contains(id)) 
    			classIds.add(id);
    	}
	}
	
	private static void removeNoNeededUseCaseId() {
    	//ȥ���������ڵ�id
    	for(Element generalizationE : generalizationElements) {
    		String removeId = generalizationE.attributeValue("parent");
    		useCaseIds.remove(removeId);
    	}
	}
	
	private static void removeNoNeededClassId() {
    	//ȥ�������ӽڵ�id
    	for(Element generalizationE : generalizationElements) {
    		String removeId = generalizationE.attributeValue("child");
    		classIds.remove(removeId);
    		
    		//*����ͼ��û�б�Ǹ��ڵ�ΪEntity XUGDG ������в���
    		String parentId = generalizationE.attributeValue("parent");
    		classIds.add(parentId);
    	}
	}
}
