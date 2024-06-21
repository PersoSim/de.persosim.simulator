package de.persosim.simulator.perso;

import java.io.StringReader;
import java.security.KeyPair;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author amay
 *
 */
public class ProfileUB01 extends AbstractProfile {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = PersonalizationDataContainer.getDefaultContainer();
		persoDataContainer.setDg1PlainData("UB");
//		persoDataContainer.setDg3PlainData("20291031");
		persoDataContainer.setDg3PlainData("20340630");
		persoDataContainer.setDg4PlainData("MAIJA");
		persoDataContainer.setDg5PlainData("MUSTERMANN");
		persoDataContainer.setDg8PlainData("19880421");
		persoDataContainer.setDg9PlainData("KANKAANPÄÄ");
		persoDataContainer.setDg10PlainData("FIN");
		persoDataContainer.setDg13PlainData("MEIKÄLÄINEN");
		persoDataContainer.setDg17StreetPlainData("HEIDESTRAẞE 17");
		persoDataContainer.setDg17CityPlainData("KÖLN");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("51147");
		persoDataContainer.setDg18PlainData("02760503150000");
		persoDataContainer.setEfCardAccess("3181C13012060A04007F0007020204020202010202010D300D060804007F00070202020201023012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C");
		persoDataContainer.setEfCardSecurity("308209D406092A864886F70D010702A08209C5308209C1020103310F300D06096086480165030402020500308203D8060804007F0007030201A08203CA048203C6318203C23012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000467C26931380033AF00024BA205E8BC592426387A3AB77B38C5996852F3279092570A545BC66A440B2157307CA931CEC064F031AA0D14AC95391ED4B95C5F537E0201293081A3060804007F00070202083181963012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D3062060904007F0007020201023052300C060704007F0007010202010D034200044F12F4460B4C3AB3EABA909929525738818277115E0EDC89C1ACCD2C68BE70651F1E23FC7B34F0A95C98A6F5817C534DA3883E9A0A7D5979485E48FDA98AED1D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C308201E6060804007F0007020207308201D8300B0609608648016503040204308201C73021020101041C8AC93144E93B0B69419544A9893160952DDE1731EA8E669606A18D203021020102041C37823963B71AF0BF5698D1FDC30DA2B7F9ECE57CFA4959BEE9D6D9943021020103041C5304B09FE96C45572D90ADAB5FDBDCE3900CB1EB1FD246233EF34D473021020104041C61CDFCE3B7F10E5AAFB363C91E921ECC7535EE7D8E8C4647BDAED7B23021020105041CA90F28EB7A0FA0DE83ABF3293D14E0838B9C85FC7277CBB97737A32B3021020106041C712B8550E49A13C64DCED4457E9A0F5A85DC26CD6A321596723005D63021020107041C42A8FA36B60887ED022CD3B6ECC255220FBE8CB3F607E416601FCAA63021020108041C85BFB42CDDA133E4AA07B4EFE4E9DC6CF514D0EA643C4DAA9C846A613021020109041C4819B180FF4FE7411E2CA99F693F590FD775CB93775D512F4E7FA6A3302102010A041CB2291AC6815E6E6A78C14FF5C7EFFEE2F0A059B5901C2C8F8B7D47E1302102010D041C8A05F63AD0BF7BDE3F3D289EE6739013C243321AD2F5724493C6B2103021020111041CAADEE20557D41AB9969E962282CAF25904475148D329D2F6B2F43E343021020112041C57CE396CA707B96FA37C580F693230E4D4AEBB97293F0909489D95CBA082049530820491308203F6A00302010202020551300A06082A8648CE3D0403043046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E79301E170D3234303630333038353631355A170D3335303730333233353935395A3057310B3009060355040613024445310C300A060355040A0C03425349310D300B0603550405130430303133312B302906035504030C225445535420446F63756D656E74205369676E65722065494420446F63756D656E7473308201B53082014D06072A8648CE3D020130820140020101303C06072A8648CE3D01010231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B412B1DA197FB71123ACD3A729901D1A71874700133107EC53306404307BC382C63D8C150C3C72080ACE05AFA0C2BEA28E4FB22787139165EFBA91F90F8AA5814A503AD4EB04A8C7DD22CE2826043004A8C7DD22CE28268B39B55416F0447C2FB77DE107DCD2A62E880EA53EEB62D57CB4390295DBC9943AB78696FA504C110461041D1C64F068CF45FFA2A63A81B7C13F6B8847A3E77EF14FE3DB7FCAFE0CBD10E8E826E03436D646AAEF87B2E247D4AF1E8ABE1D7520F9C2A45CB1EB8E95CFD55262B70B29FEEC5864E19C054FF99129280E4646217791811142820341263C53150231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B31F166E6CAC0425A7CF3AB6AF6B7FC3103B883202E90465650201010362000424065A4080B5947DE35625AD921FA0A54748F1302BB571E04D8DA712C44B3FD8D85A4A5E7FA632F3E7A4B7C767215780747C1E6D108706469AFE875F1890E3365888B48C656D96B49FA17F912EBADBA006A67DFFC9D5789CC0A6F485DE5DD40DA38201633082015F301F0603551D23041830168014E4F934EE5ED98D61C3F2EF1A49F290801D08FBB9301D0603551D0E041604143B71F1EA3C875BE7FAAE7878C2C75A328F43B18E300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303234303630333038353631355A810F32303235303730333233353935395A30160603551D20040F300D300B060904007F00070301010130260603551D11041F301D820B6273692E62756E642E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443016060767810801010602040B300902010031041302554230350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D040304038188003081840240661ABBC70D25DE29CECB2E4F6B31646A2DCAB33378BDF9ECD30EA4F5F290283BCA1616E47F512962E9EB764089104528B9B981769C9982F32187B1136B2F4F2202405C9D8301873BB3B64A2EE620E2A38A4F9C49038938BBEF9989039E061EFF3B8A71D8B77C2553A0C3CDBEFCC69F8B18758BFB42EFE6ABCE0B9482066768503D3C3182013430820130020101304C3046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E7902020551300D06096086480165030402020500A05A301706092A864886F70D010903310A060804007F0007030201303F06092A864886F70D010904313204309A299BEF65F56DB4EE204876C4D354B4009595BDB561E220F56BF47EFD2C8081AA5433711A93F702C325F55DB031BC46300A06082A8648CE3D0403030466306402305BA6270B799362BE184FCDFD5F74DCBC7D414A78954F25A42ADBDC1E7846FE36F5110EB084FB6A47A4AF300A8CDE7878023076EBBB60B46A715DE2050F5A5C1E6158FC5F4F5FC4F7A61A7DF999AF8E8D8AB18965ED535F4F18DA978ED6AD7F7A0331");
		persoDataContainer.setEfChipSecurity("308209D406092A864886F70D010702A08209C5308209C1020103310F300D06096086480165030402020500308203D8060804007F0007030201A08203CA048203C6318203C23012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000467C26931380033AF00024BA205E8BC592426387A3AB77B38C5996852F3279092570A545BC66A440B2157307CA931CEC064F031AA0D14AC95391ED4B95C5F537E0201293081A3060804007F00070202083181963012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D3062060904007F0007020201023052300C060704007F0007010202010D034200044F12F4460B4C3AB3EABA909929525738818277115E0EDC89C1ACCD2C68BE70651F1E23FC7B34F0A95C98A6F5817C534DA3883E9A0A7D5979485E48FDA98AED1D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C308201E6060804007F0007020207308201D8300B0609608648016503040204308201C73021020101041C8AC93144E93B0B69419544A9893160952DDE1731EA8E669606A18D203021020102041C37823963B71AF0BF5698D1FDC30DA2B7F9ECE57CFA4959BEE9D6D9943021020103041C5304B09FE96C45572D90ADAB5FDBDCE3900CB1EB1FD246233EF34D473021020104041C61CDFCE3B7F10E5AAFB363C91E921ECC7535EE7D8E8C4647BDAED7B23021020105041CA90F28EB7A0FA0DE83ABF3293D14E0838B9C85FC7277CBB97737A32B3021020106041C712B8550E49A13C64DCED4457E9A0F5A85DC26CD6A321596723005D63021020107041C42A8FA36B60887ED022CD3B6ECC255220FBE8CB3F607E416601FCAA63021020108041C85BFB42CDDA133E4AA07B4EFE4E9DC6CF514D0EA643C4DAA9C846A613021020109041C4819B180FF4FE7411E2CA99F693F590FD775CB93775D512F4E7FA6A3302102010A041CB2291AC6815E6E6A78C14FF5C7EFFEE2F0A059B5901C2C8F8B7D47E1302102010D041C8A05F63AD0BF7BDE3F3D289EE6739013C243321AD2F5724493C6B2103021020111041CAADEE20557D41AB9969E962282CAF25904475148D329D2F6B2F43E343021020112041C57CE396CA707B96FA37C580F693230E4D4AEBB97293F0909489D95CBA082049530820491308203F6A00302010202020551300A06082A8648CE3D0403043046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E79301E170D3234303630333038353631355A170D3335303730333233353935395A3057310B3009060355040613024445310C300A060355040A0C03425349310D300B0603550405130430303133312B302906035504030C225445535420446F63756D656E74205369676E65722065494420446F63756D656E7473308201B53082014D06072A8648CE3D020130820140020101303C06072A8648CE3D01010231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B412B1DA197FB71123ACD3A729901D1A71874700133107EC53306404307BC382C63D8C150C3C72080ACE05AFA0C2BEA28E4FB22787139165EFBA91F90F8AA5814A503AD4EB04A8C7DD22CE2826043004A8C7DD22CE28268B39B55416F0447C2FB77DE107DCD2A62E880EA53EEB62D57CB4390295DBC9943AB78696FA504C110461041D1C64F068CF45FFA2A63A81B7C13F6B8847A3E77EF14FE3DB7FCAFE0CBD10E8E826E03436D646AAEF87B2E247D4AF1E8ABE1D7520F9C2A45CB1EB8E95CFD55262B70B29FEEC5864E19C054FF99129280E4646217791811142820341263C53150231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B31F166E6CAC0425A7CF3AB6AF6B7FC3103B883202E90465650201010362000424065A4080B5947DE35625AD921FA0A54748F1302BB571E04D8DA712C44B3FD8D85A4A5E7FA632F3E7A4B7C767215780747C1E6D108706469AFE875F1890E3365888B48C656D96B49FA17F912EBADBA006A67DFFC9D5789CC0A6F485DE5DD40DA38201633082015F301F0603551D23041830168014E4F934EE5ED98D61C3F2EF1A49F290801D08FBB9301D0603551D0E041604143B71F1EA3C875BE7FAAE7878C2C75A328F43B18E300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303234303630333038353631355A810F32303235303730333233353935395A30160603551D20040F300D300B060904007F00070301010130260603551D11041F301D820B6273692E62756E642E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443016060767810801010602040B300902010031041302554230350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D040304038188003081840240661ABBC70D25DE29CECB2E4F6B31646A2DCAB33378BDF9ECD30EA4F5F290283BCA1616E47F512962E9EB764089104528B9B981769C9982F32187B1136B2F4F2202405C9D8301873BB3B64A2EE620E2A38A4F9C49038938BBEF9989039E061EFF3B8A71D8B77C2553A0C3CDBEFCC69F8B18758BFB42EFE6ABCE0B9482066768503D3C3182013430820130020101304C3046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E7902020551300D06096086480165030402020500A05A301706092A864886F70D010903310A060804007F0007030201303F06092A864886F70D010904313204309A299BEF65F56DB4EE204876C4D354B4009595BDB561E220F56BF47EFD2C8081AA5433711A93F702C325F55DB031BC46300A06082A8648CE3D0403030466306402305BA6270B799362BE184FCDFD5F74DCBC7D414A78954F25A42ADBDC1E7846FE36F5110EB084FB6A47A4AF300A8CDE7878023076EBBB60B46A715DE2050F5A5C1E6158FC5F4F5FC4F7A61A7DF999AF8E8D8AB18965ED535F4F18DA978ED6AD7F7A0331");
		
		String documentNumber = "00000UB01";
		String sex = "F";
		String mrzLine3 = "MUSTERMANN<<MAIJA<<<<<<<<<<<<<";
		String mrz = persoDataContainer.createMrzFromDgs(documentNumber, sex, mrzLine3);
		
		persoDataContainer.setMrz(mrz);
		persoDataContainer.setEpassDg1PlainData(mrz);
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0467C26931380033AF00024BA205E8BC592426387A3AB77B38C5996852F3279092570A545BC66A440B2157307CA931CEC064F031AA0D14AC95391ED4B95C5F537E"),
				HexString.toByteArray("751B90F43087F3295E8BD811F8290F2504D06602DA8CD5A2EAAF53FB660C634C")),
				41, false);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("044F12F4460B4C3AB3EABA909929525738818277115E0EDC89C1ACCD2C68BE70651F1E23FC7B34F0A95C98A6F5817C534DA3883E9A0A7D5979485E48FDA98AED1D"),
				HexString.toByteArray("4837AAF61C61CA7D271758FEE30C49D48A203F445C94E4D70651FD4CC6755C14")),
				45, true);
		
		// individual RI key - 1st sector public/private key pair (Sperrmerkmal)
		KeyPair riKeyPair1 = (KeyPair)PersonalizationFactory.unmarshal(new StringReader("""
				<java.security.KeyPair id="1">
				  <privateKey id="2">
				    <algorithm>ECDH</algorithm>
				    <value>3082024B0201003081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A70201010482015530820151020101042049ECAD5DBB46FCF5B691B11091F617F95A2C1D7E82BFA16397E32CE1C78A9F41A081E33081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101A14403420004696C35C3C23B95E8D685BB734D6DF5A87293B943CC44CAC44984F8A345F712F39A121C6AEC76CA183D815554761FEA88153EF107AA68279205E28C4E298E63AB</value>
				  </privateKey>
				  <publicKey id="3">
				    <algorithm>ECDH</algorithm>
				    <value>308201333081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A702010103420004696C35C3C23B95E8D685BB734D6DF5A87293B943CC44CAC44984F8A345F712F39A121C6AEC76CA183D815554761FEA88153EF107AA68279205E28C4E298E63AB</value>
				  </publicKey>
				</java.security.KeyPair>						
		"""));
		persoDataContainer.addRiKeyPair(riKeyPair1, 1, false);

		// individual RI key - 2nd sector public/private key pair (Pseudonym)
		KeyPair riKeyPair2 = (KeyPair)PersonalizationFactory.unmarshal(new StringReader("""
				<java.security.KeyPair id="1">
				  <privateKey id="2">
				    <algorithm>ECDH</algorithm>
				    <value>3082024B0201003081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101048201553082015102010104209D51BF2E8F1D7211801D788667C76E74D711ECA7D01EDEB408766C59719C1930A081E33081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101A144034200047C8055130F5F9733E6B3C8E4B91D862EF8EFFFA0FB8DCFDF5AE3D01641670B42093BE7F504ACD01D74E169DE132364847481040DE75ABC62CD649C7A92F37851</value>
				  </privateKey>
				  <publicKey id="3">
				    <algorithm>ECDH</algorithm>
				    <value>308201333081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101034200047C8055130F5F9733E6B3C8E4B91D862EF8EFFFA0FB8DCFDF5AE3D01641670B42093BE7F504ACD01D74E169DE132364847481040DE75ABC62CD649C7A92F37851</value>
				  </publicKey>
				</java.security.KeyPair>						
		"""));
		persoDataContainer.addRiKeyPair(riKeyPair2, 2, true);
	}

}