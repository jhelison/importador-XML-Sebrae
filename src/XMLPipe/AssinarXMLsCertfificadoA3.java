package XMLPipe;
import java.awt.print.Printable;
import java.io.BufferedReader;  
import java.io.ByteArrayInputStream;  
import java.io.ByteArrayOutputStream;  
import java.io.FileInputStream;  
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;  
import java.security.InvalidAlgorithmParameterException;  
import java.security.KeyStore;  
import java.security.NoSuchAlgorithmException;  
import java.security.PrivateKey;  
import java.security.Provider;  
import java.security.Security;  
import java.security.cert.X509Certificate;  
import java.util.ArrayList;  
import java.util.Collections;  
import java.util.Enumeration;  
import java.util.List;  
  
import javax.xml.crypto.dsig.CanonicalizationMethod;  
import javax.xml.crypto.dsig.DigestMethod;  
import javax.xml.crypto.dsig.Reference;  
import javax.xml.crypto.dsig.SignatureMethod;  
import javax.xml.crypto.dsig.SignedInfo;  
import javax.xml.crypto.dsig.Transform;  
import javax.xml.crypto.dsig.XMLSignature;  
import javax.xml.crypto.dsig.XMLSignatureFactory;  
import javax.xml.crypto.dsig.dom.DOMSignContext;  
import javax.xml.crypto.dsig.keyinfo.KeyInfo;  
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;  
import javax.xml.crypto.dsig.keyinfo.X509Data;  
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;  
import javax.xml.crypto.dsig.spec.TransformParameterSpec;  
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.transform.Transformer;  
import javax.xml.transform.TransformerException;  
import javax.xml.transform.TransformerFactory;  
import javax.xml.transform.dom.DOMSource;  
import javax.xml.transform.stream.StreamResult;  
  
import org.w3c.dom.Document;  
import org.w3c.dom.NodeList;  
import org.xml.sax.SAXException;  
  
public class AssinarXMLsCertfificadoA3 {  

    private static final String EVENTO = "evento";
    private static final String NFE = "NFe";
    private static final String INFNFE = "infNFe";
  
    private PrivateKey privateKey;  
    private KeyInfo keyInfo;  

    private static String certType;
    private static String xmlEnviNFeAssinado;

    public static String assinarArquivo(String senhaDoCertificadoDoCliente, String fileEnviNFe, String certipe) {
    	certType = certipe;
        try {  
            AssinarXMLsCertfificadoA3 assinarXMLsCertfificadoA3 = new AssinarXMLsCertfificadoA3();  
  
            info("");  
            String xmlEnviNFe = lerXML(fileEnviNFe);  
            xmlEnviNFeAssinado = assinarXMLsCertfificadoA3.assinaEnviNFe(xmlEnviNFe, senhaDoCertificadoDoCliente);  
        }
        catch (Exception e) {  
            e.printStackTrace();  
            error("| " + e.toString());  
        }

        //xmlEnviNFeAssinado = xmlEnviNFeAssinado.replaceAll("<?xml([\\s\\S\\n]*?)?>", "");

        return xmlEnviNFeAssinado;
    }  
  
    public String assinaEnviNFe(String xml, String senha) throws Exception {  
        Document document = documentFactory(xml);  
        XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");  
        ArrayList<Transform> transformList = signatureFactory(signatureFactory);  
        loadCertificates(senha, signatureFactory);  

        //System.out.println("| INFO: " + document.getDocumentElement().getElementsByTagName(NFE).getLength());

        //for (int i = 0; i < document.getDocumentElement().getElementsByTagName(NFE).getLength(); i++) {
		//System.out.println("| INFO: " + "Func�o assinar");
        assinar(NFE, signatureFactory, transformList, privateKey, keyInfo, document, 0);  
        //}

        return outputXML(document);  
    }

    private String outputXML(Document doc) throws TransformerException {
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
    	TransformerFactory tf = TransformerFactory.newInstance();
    	Transformer trans = tf.newTransformer();
    	trans.transform(new DOMSource(doc), new StreamResult(os));
    	String xml = os.toString();
    	if ((xml != null) && (!"".equals(xml))) {
    		xml = xml.replaceAll("\\r\\n", "");
    		xml = xml.replaceAll(" standalone=\"no\"", "");
    	}
    	
    	return xml;
    }
  
    private void assinar(String tipo, XMLSignatureFactory fac, ArrayList<Transform> transformList, PrivateKey privateKey,  KeyInfo ki, Document document, int index) throws Exception {
        NodeList elements = null;
  
        elements = document.getElementsByTagName("infNFe");

        org.w3c.dom.Element el = (org.w3c.dom.Element) elements.item(index);  
        String id = el.getAttribute("Id");
        el.setIdAttribute("Id", true);
  
        Reference ref = fac.newReference("#" + id, fac.newDigestMethod(DigestMethod.SHA1, null), transformList, null, null);  
  
        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(  
                CanonicalizationMethod.INCLUSIVE,  
                (C14NMethodParameterSpec) null), fac  
                .newSignatureMethod(SignatureMethod.RSA_SHA1, null),  
                Collections.singletonList(ref));  
  
        XMLSignature signature = fac.newXMLSignature(si, ki);  
  
        //System.out.println("| INFO: " +  document.getFirstChild());
    	DOMSignContext dsc = new DOMSignContext(privateKey, document.getFirstChild());
    	signature.sign(dsc);
    }  

    private void loadCertificates(String senha, XMLSignatureFactory signatureFactory) throws Exception {  
  
        /** 
         * Para Certificados A3 Cartao usar: SmartCard.cfg; 
         * Para Certificados A3 Token usar: Token.cfg; 
         */
    	Provider provider;
    	if(certType.equals("Card")) {
    		provider = new sun.security.pkcs11.SunPKCS11("SmartCard.cfg");
    	}
    	else {
    		provider = new sun.security.pkcs11.SunPKCS11("Token.cfg");
    	}
        Security.addProvider(provider);   
  
        KeyStore ks = KeyStore.getInstance("pkcs11", provider);  
        try {  
            ks.load(null, senha.toCharArray());  
        }
        catch (IOException e) {  
            throw new Exception("Senha do Certificado Digital incorreta ou Certificado inv�lido.");  
        }  
  
        KeyStore.PrivateKeyEntry pkEntry = null;  
        Enumeration<String> aliasesEnum = ks.aliases();  
        while (aliasesEnum.hasMoreElements()) {  
            String alias = (String) aliasesEnum.nextElement();  
            if (ks.isKeyEntry(alias)) {  
                pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(alias, new KeyStore.PasswordProtection(senha.toCharArray()));  
                privateKey = pkEntry.getPrivateKey();  
                break;  
            }  
        }  
  
        X509Certificate cert = (X509Certificate) pkEntry.getCertificate();  
        info("SubjectDN: " + cert.getSubjectDN().toString());  
  
        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();  
        List<X509Certificate> x509Content = new ArrayList<X509Certificate>();  
  
        x509Content.add(cert);  
        X509Data x509Data = keyInfoFactory.newX509Data(x509Content);  
        keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));  
    }

    private ArrayList<Transform> signatureFactory(XMLSignatureFactory signatureFactory) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {  
        ArrayList<Transform> transformList = new ArrayList<Transform>();  
        TransformParameterSpec tps = null;  
        Transform envelopedTransform = signatureFactory.newTransform(Transform.ENVELOPED, tps);  
        Transform c14NTransform = signatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315", tps);  

        transformList.add(envelopedTransform);  
        transformList.add(c14NTransform);  
        return transformList;  
    }

    private Document documentFactory(String xml) throws SAXException, IOException, ParserConfigurationException {  
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        factory.setNamespaceAware(true);  
        Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));  
        return document;  
    }  
  
    private static String lerXML(String fileXML) throws IOException {  
        String linha = "";  
        StringBuilder xml = new StringBuilder();  
  
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileXML)));  
        while ((linha = in.readLine()) != null) {  
            xml.append(linha);  
        }  
        in.close();
        String str = xml.toString();

        str = str.replaceAll("<NFe([\\s\\S\\n]*?)>", "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">");
  
        str = str.replaceAll("<verProc>([\\s\\S\\n]*?)</verProc>", "<verProc>4.01_b029</verProc>");

        return str;  
    }  
  
    private static void error(String error) {  
        //System.out.println("| ERROR: " + error);  
    }  
  
    private static void info(String info) {  
        //System.out.println("| INFO: " + info);  
    }  
} 