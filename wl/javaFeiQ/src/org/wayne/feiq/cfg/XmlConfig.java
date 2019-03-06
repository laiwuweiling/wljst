package org.wayne.feiq.cfg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.swt.graphics.Rectangle;
import org.wayne.feiq.FeiQException;
import org.wayne.feiq.util.Util;

public class XmlConfig extends Config {

	private static final Log log = LogFactory.getLog(XmlConfig.class);

	private static final File FILE = new File("cfg/javafeiq.xml");
	private Document doc;

	public XmlConfig() {
		InputStream in = null;
		try {
			in = new FileInputStream(FILE);
			init(in);
		} catch (Exception e) {
			throw new FeiQException(e);
		} finally {
			Util.closeQuietly(in, log);
		}
	}

	private XmlConfig(boolean none) {
		doc = new DOMDocument();
		Element root = new DOMElement("΢�켴ʱͨ");
		doc.setRootElement(root);
		root.addElement("sendMsgShortcut");
		root.addElement("defaultSavePath");
		root.addElement("msgSound");
		root.addElement("mainShell");
	}

	private void init(InputStream in) throws DocumentException {
		SAXReader reader = new SAXReader(false);
		doc = reader.read(in);
		Element root = doc.getRootElement();
		setSendMsgShortcut(Byte.parseByte(root
				.elementTextTrim("sendMsgShortcut")));
		setDefaultSavePath(root.elementTextTrim("defaultSavePath"));
		setMsgSound(root.elementTextTrim("msgSound"));
		Element eMainShell = root.element("mainShell");
		setMainShellRec(new Rectangle(Integer.parseInt(eMainShell
				.attributeValue("x")), Integer.parseInt(eMainShell
				.attributeValue("y")), Integer.parseInt(eMainShell
				.attributeValue("width")), Integer.parseInt(eMainShell
				.attributeValue("height"))));
	}

	public static XmlConfig getDefault() {
		return new XmlConfig(true);
	}

	public static boolean isFileExists() {
		return FILE.exists();
	}

	public void save() {
		Element root = doc.getRootElement();
		root.element("defaultSavePath").setText(defaultSavePath);
		root.element("sendMsgShortcut").setText(Byte.toString(sendMsgShortcut));
		root.element("msgSound").setText(msgSound);
		root.element("mainShell").addAttribute("x",
				Integer.toString(mainShellRec.x)).addAttribute("y",
				Integer.toString(mainShellRec.y)).addAttribute("width",
				Integer.toString(mainShellRec.width)).addAttribute("height",
				Integer.toString(mainShellRec.height));
		try {
			File parent = FILE.getParentFile();
			if (!parent.exists())
				parent.mkdir();
			FileOutputStream fos = new FileOutputStream(FILE);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos,
					"utf-8"));
			XMLWriter writer = new XMLWriter(bw);
			writer.write(doc);
			bw.close();
		} catch (IOException e) {
			log.error("save " + FILE.getPath() + " failed", e);
		}
	}

}
