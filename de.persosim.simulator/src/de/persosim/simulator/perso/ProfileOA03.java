package de.persosim.simulator.perso;

import java.util.List;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.SecInfoProtocol;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.utils.HexString;

/**
 * @author mboonk
 *
 */
public class ProfileOA03 extends ProfileOA {

	@Override
	protected List<Protocol> buildProtocolList() {
		List<Protocol> protocolList = super.buildProtocolList();
		protocolList.add(new SecInfoProtocol());
		return protocolList;
	}

	@Override
	public void setPersoDataContainer() {
		persoDataContainer = PersonalizationDataContainer.getDefaultContainer();
		persoDataContainer.setDg1PlainData("OA");
		persoDataContainer.setDg2PlainData("ESP");
		persoDataContainer.setDg3PlainData("20230831");
		persoDataContainer.setDg4PlainData("MARIA ELENA INÊS");
		persoDataContainer.setDg5PlainData("GARCIA LÓPEZ");
		persoDataContainer.setDg6PlainData("SCHWESTER ANNA");
		persoDataContainer.setDg7PlainData("DR.");
		persoDataContainer.setDg8PlainData("19640812");
		persoDataContainer.setDg9PlainData("GRANADA");
		persoDataContainer.setDg10PlainData("D");
		persoDataContainer.setDg17StreetPlainData("AMTSPLATZ 4A");
		persoDataContainer.setDg17CityPlainData("EISENACH");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("'99817");
		persoDataContainer.setDg18PlainData("02761600560000");
		persoDataContainer.setEfCardAccess(
				"3181C13012060A04007F0007020204020202010202010D300D060804007F00070202020201023012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C");
		persoDataContainer.setEfCardSecurity(
				"308209F606092A864886F70D010702A08209E7308209E3020103310F300D06096086480165030402030500308203E0060804007F0007030201A08203D2048203CE318203CA3012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003021060904007F000702020502301406072A8648CE3D020106092B24030302080101073017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F80201293081A3060804007F00070202083181963012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D3062060904007F0007020201023052300C060704007F0007010202010D034200041AC6CAE884A6C2B8461404150F54CD1150B21E862A4E5F21CE34290C741104BD1BF31ED91E085D7C630E8B4D10A8AE22BBB2898B44B52EA0F4CDADCF57CFBA2502012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C308201E6060804007F0007020207308201D8300B0609608648016503040204308201C73021020101041C2FF0247F59DD3C646E314F03ABB33EE91A586577EBDF48D3864EC34D3021020102041C37823963B71AF0BF5698D1FDC30DA2B7F9ECE57CFA4959BEE9D6D9943021020103041CE8B2A171DC1290A765F124AAFE33061C08C918A1069DFF5CAF4C62B53021020104041CAD81D20DBD4F5687FDB05E5037EC267609FDE28C6036FDBDF2C8B4333021020105041CA90F28EB7A0FA0DE83ABF3293D14E0838B9C85FC7277CBB97737A32B3021020106041C712B8550E49A13C64DCED4457E9A0F5A85DC26CD6A321596723005D63021020107041C42A8FA36B60887ED022CD3B6ECC255220FBE8CB3F607E416601FCAA63021020108041C6446E0A909967462B5C1117634F8A1B557EF74BE3F606C1E94EFAE433021020109041C635D1017F4ABC656B9FDDDD7E0FBB1E992B7686E89485E6AB51B638B302102010D041C04DB93544A64BC1245B10AAB266386F08F8E89F72E1DB178C172624D3021020111041CAADEE20557D41AB9969E962282CAF25904475148D329D2F6B2F43E343021020112041C57CE396CA707B96FA37C580F693230E4D4AEBB97293F0909489D95CB302102010A041C1880A259CDB497C15A7FDD1C9AC9490D7DC0D18743378603D43D1D4FA082049F3082049B308203FEA003020102020204D5300A06082A8648CE3D0403043046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E79301E170D3230303132313036333630345A170D3330303832313233353935395A305C310B3009060355040613024445310C300A060355040A0C03425349310D300B06035504051304303039393130302E06035504030C275445535420446F63756D656E74205369676E6572204964656E7469747920446F63756D656E7473308201B53082014D06072A8648CE3D020130820140020101303C06072A8648CE3D01010231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B412B1DA197FB71123ACD3A729901D1A71874700133107EC53306404307BC382C63D8C150C3C72080ACE05AFA0C2BEA28E4FB22787139165EFBA91F90F8AA5814A503AD4EB04A8C7DD22CE2826043004A8C7DD22CE28268B39B55416F0447C2FB77DE107DCD2A62E880EA53EEB62D57CB4390295DBC9943AB78696FA504C110461041D1C64F068CF45FFA2A63A81B7C13F6B8847A3E77EF14FE3DB7FCAFE0CBD10E8E826E03436D646AAEF87B2E247D4AF1E8ABE1D7520F9C2A45CB1EB8E95CFD55262B70B29FEEC5864E19C054FF99129280E4646217791811142820341263C53150231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B31F166E6CAC0425A7CF3AB6AF6B7FC3103B883202E90465650201010362000401B434B9555974F51934687C520DAE338032F5046999E1595D85B89A4CBDB90888B8DCAB2D6588CF73E8E43DB78AB40A0FDB710D971F1C0205B9243E1F769A9E0681C01D1B298C4D7DE7F3F7E6CE9F16657907B79328BEC8166F5FC035E26EE3A382016630820162301F0603551D23041830168014539DB1872AAC9193D76392EE80D9E5996CF99B3B301D0603551D0E0416041472571E58FC52EAD9641412875C615E8090508CFA300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303230303132313036333630345A810F32303230303832313233353935395A30160603551D20040F300D300B060904007F00070301010130260603551D11041F301D820B6273692E62756E642E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443019060767810801010602040E300C02010031071301411302494430350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D04030403818A00308186024100A348C5E7948535C9ECB5043D62FA1F56F16886AF76C434C870D988D345175FD51E60A89C0E9D06A94D35078853397D7C8403E32053DF6BDFC16CC1B3A5E7D1CB0241008506DC6ACA4F202B4BDF7957263010886D38D4991D101374F6A7B8F4BC1CE51CB278E9F8851951F6AF0ABA7D4773F42762FD8F840A01F2D526CC80682DCA08103182014430820140020101304C3046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E79020204D5300D06096086480165030402030500A06A301706092A864886F70D010903310A060804007F0007030201304F06092A864886F70D0109043142044066927654D73A84CCCD931E2C44A9B34EF3B848EE85B7F4A92699EA7BF5262FE73B101F31F580180C96EA642569E5E6DB8469A4C7E4CB47DFE9C5D95B0939125E300A06082A8648CE3D040304046630640230582364C74D9C694D3C8F99ACBF82A7A847141248B015AED8BEE3C395E82788426F032978D196303A6B81D9FA8B8DBC8E02305BF169DE97B344A4B03E862C48A76226F044C6DA1EA78E380C2C6479B79526415735345764D7B6E738EE83931AABE840");
		persoDataContainer.setEfChipSecurity(
				"308207A606092A864886F70D010702A082079730820793020103310F300D0609608648016503040203050030820190060804007F0007030201A08201820482017E3182017A3012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003021060904007F000702020502301406072A8648CE3D020106092B24030302080101073017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6CA082049F3082049B308203FEA003020102020204D5300A06082A8648CE3D0403043046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E79301E170D3230303132313036333630345A170D3330303832313233353935395A305C310B3009060355040613024445310C300A060355040A0C03425349310D300B06035504051304303039393130302E06035504030C275445535420446F63756D656E74205369676E6572204964656E7469747920446F63756D656E7473308201B53082014D06072A8648CE3D020130820140020101303C06072A8648CE3D01010231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B412B1DA197FB71123ACD3A729901D1A71874700133107EC53306404307BC382C63D8C150C3C72080ACE05AFA0C2BEA28E4FB22787139165EFBA91F90F8AA5814A503AD4EB04A8C7DD22CE2826043004A8C7DD22CE28268B39B55416F0447C2FB77DE107DCD2A62E880EA53EEB62D57CB4390295DBC9943AB78696FA504C110461041D1C64F068CF45FFA2A63A81B7C13F6B8847A3E77EF14FE3DB7FCAFE0CBD10E8E826E03436D646AAEF87B2E247D4AF1E8ABE1D7520F9C2A45CB1EB8E95CFD55262B70B29FEEC5864E19C054FF99129280E4646217791811142820341263C53150231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B31F166E6CAC0425A7CF3AB6AF6B7FC3103B883202E90465650201010362000401B434B9555974F51934687C520DAE338032F5046999E1595D85B89A4CBDB90888B8DCAB2D6588CF73E8E43DB78AB40A0FDB710D971F1C0205B9243E1F769A9E0681C01D1B298C4D7DE7F3F7E6CE9F16657907B79328BEC8166F5FC035E26EE3A382016630820162301F0603551D23041830168014539DB1872AAC9193D76392EE80D9E5996CF99B3B301D0603551D0E0416041472571E58FC52EAD9641412875C615E8090508CFA300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303230303132313036333630345A810F32303230303832313233353935395A30160603551D20040F300D300B060904007F00070301010130260603551D11041F301D820B6273692E62756E642E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443019060767810801010602040E300C02010031071301411302494430350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D04030403818A00308186024100A348C5E7948535C9ECB5043D62FA1F56F16886AF76C434C870D988D345175FD51E60A89C0E9D06A94D35078853397D7C8403E32053DF6BDFC16CC1B3A5E7D1CB0241008506DC6ACA4F202B4BDF7957263010886D38D4991D101374F6A7B8F4BC1CE51CB278E9F8851951F6AF0ABA7D4773F42762FD8F840A01F2D526CC80682DCA08103182014430820140020101304C3046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E79020204D5300D06096086480165030402030500A06A301706092A864886F70D010903310A060804007F0007030201304F06092A864886F70D0109043142044064C378E9D347491D8BCEFD9BA004B301644A8601CABF3ECA478D531D9B5C7CE2E0169FAF3C1ABDE266914DBAFD560812BD07882AE049018C9B19600596632033300A06082A8648CE3D04030404663064023044EFA8FBB0D0DBD11BD5E0029C2C262A168101FB2A43B6BE5651AE7003092D8AFE29D64BEF66186B00E16863353274B7023066B78BE50D7D74B2B841BC9D3E69CABEDB67503DDE5907D69DF553CB9C80C9252D63E9716088A1182295628BA22E427E");

		String documentNumber = "000000003";
		String sex = "F";
		String mrzLine3 = "GARCIA<LOPEZ<<MARIA<ELENA<INES";
		String mrz = persoDataContainer.createMrzFromDgs(documentNumber, sex, mrzLine3);

		persoDataContainer.setMrz(mrz);
		persoDataContainer.setEpassDg1PlainData(mrz);

		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13, HexString.toByteArray(
				"0419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8"),
				HexString.toByteArray("A07EB62E891DAA84643E0AFCC1AF006891B669B8F51E379477DBEAB8C987A610")), 41, false);

		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13, HexString.toByteArray(
				"041AC6CAE884A6C2B8461404150F54CD1150B21E862A4E5F21CE34290C741104BD1BF31ED91E085D7C630E8B4D10A8AE22BBB2898B44B52EA0F4CDADCF57CFBA25"),
				HexString.toByteArray("763B6BBF8A7DFC5DAB3205791BA64D211BBC4E8A5C531C77488792C508BD3D1E")), 45, true);

		// individual RI key - 1st sector public/private key pair (Sperrmerkmal)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13, HexString.toByteArray(
				"040F6CB0699AC257E58CE95E83DFEAE11DC486F52889885B3F9CB65765921838BB00F776C455D57F4D483C428FB15C887D768D58BAEF599BD192CCAE7F98AF41CA"),
				HexString.toByteArray("0353859C2EC67780BA39015DE8C682AF2326D43DE9CE1E07737087BD1E17CB22")), 1, false);

		// individual RI key - 2nd sector public/private key pair (Pseudonym)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13, HexString.toByteArray(
				"0483FA15CAE8E4E2BB06D450FDA72A7E341C2E85F80B0F88A901D3A4DFEEC1C80B2B060CC6E4EE8ADF649B78A8BF781F6B03142DF9C44AB1913FF5A53F72A45D97"),
				HexString.toByteArray("009AD0AD7F4DFAAA06988339FC31D3A111F4C7964AC7F377373A2454327C43E2FF")), 2, true);

		addMobileOid(Tr03110.id_mobileEIDType_SECertified);

	}

}
