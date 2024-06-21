package de.persosim.simulator.perso;

import java.io.StringReader;
import java.security.KeyPair;

import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class Profile08 extends AbstractProfile {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = PersonalizationDataContainer.getDefaultContainer();
		persoDataContainer.setDg3PlainData("20340630");
		persoDataContainer.setDg4PlainData("KARL");
		persoDataContainer.setDg5PlainData("HILLEBRANDT");
		persoDataContainer.setDg6PlainData("GRAF V. LÝSKY");
		persoDataContainer.setDg7PlainData("DR.HC.");
		persoDataContainer.setDg8PlainData("19720617");
		persoDataContainer.setDg9PlainData("TRIER");
		persoDataContainer.setDg10PlainData("D");
		persoDataContainer.setDg18PlainData("");
		persoDataContainer.setEfCardAccess("3181C13012060A04007F0007020204020202010202010D300D060804007F00070202020201023012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C");
		persoDataContainer.setEfCardSecurity("308209DC06092A864886F70D010702A08209CD308209C9020103310F300D06096086480165030402020500308203D8060804007F0007030201A08203CA048203C6318203C23012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC20201293081A3060804007F00070202083181963012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D3062060904007F0007020201023052300C060704007F0007010202010D0342000482ED7BDBBC67FF81507630E88819F3B001E47592D8B686D4C057FB8B75110D2E753F56C2F188337D1BCCA74CD12D7186E1AAD6D8A560DC90D56590BC373E558702012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C308201E6060804007F0007020207308201D8300B0609608648016503040204308201C73021020101041C2FF0247F59DD3C646E314F03ABB33EE91A586577EBDF48D3864EC34D3021020102041C37823963B71AF0BF5698D1FDC30DA2B7F9ECE57CFA4959BEE9D6D9943021020103041CE8B2A171DC1290A765F124AAFE33061C08C918A1069DFF5CAF4C62B53021020104041CBE76F4E545E2331C639A45F5A867DAA976C1D44046A6FABD21495B363021020105041C5E443C929B0720E64FB490290DADB57BA4419D1A7EDC2C719F1298A73021020106041CEF3303FCF83678AF2771D57E66A63C7FC2723E4293461921062B71633021020107041C874C679DFB7A537F54111CF727B2E167CDD2721AAFA2BFC37B7C3E0A3021020108041CF0889E6A073E2CC3CC02191F413482B99B75B7727FEF62C5E7716CDF3021020109041C3D0CD1DF88C19785125B44C4CC93E89339B88826E87A04DCF4B9F7633021020111041C5136C1CF924686749AA0358EF331DFD00D3C24AD574C505CB41EE6983021020112041CB2A04D6EE2B222FF387237247E648912ADD8E731CE2E128029C5F6C0302102010A041C1880A259CDB497C15A7FDD1C9AC9490D7DC0D18743378603D43D1D4F302102010D041C859FE631F5DA379D44239EB85FAFDF7D52FDBC88986B254045DCF82AA082049D30820499308203FEA00302010202020550300A06082A8648CE3D0403043046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E79301E170D3234303630333038353334355A170D3335303130333233353935395A305C310B3009060355040613024445310C300A060355040A0C03425349310D300B06035504051304303133313130302E06035504030C275445535420446F63756D656E74205369676E6572204964656E7469747920446F63756D656E7473308201B53082014D06072A8648CE3D020130820140020101303C06072A8648CE3D01010231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B412B1DA197FB71123ACD3A729901D1A71874700133107EC53306404307BC382C63D8C150C3C72080ACE05AFA0C2BEA28E4FB22787139165EFBA91F90F8AA5814A503AD4EB04A8C7DD22CE2826043004A8C7DD22CE28268B39B55416F0447C2FB77DE107DCD2A62E880EA53EEB62D57CB4390295DBC9943AB78696FA504C110461041D1C64F068CF45FFA2A63A81B7C13F6B8847A3E77EF14FE3DB7FCAFE0CBD10E8E826E03436D646AAEF87B2E247D4AF1E8ABE1D7520F9C2A45CB1EB8E95CFD55262B70B29FEEC5864E19C054FF99129280E4646217791811142820341263C53150231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B31F166E6CAC0425A7CF3AB6AF6B7FC3103B883202E904656502010103620004301BB91C96792E65620452749E319C2B62C46B8EBBE846438F098DE4416ECF63463C5518B961482D12FCDFEE2AC349C58B752A893FE94D6E87445976C6309635399E20ED50B5520D501F52835E58AEA0C2A57CC6CFBC7F806228C58F18B0630DA382016630820162301F0603551D23041830168014E4F934EE5ED98D61C3F2EF1A49F290801D08FBB9301D0603551D0E041604149D144787CA96BA8A3325F84BE499114A798CA3C0300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303234303630333038353334355A810F32303235303130333233353935395A30160603551D20040F300D300B060904007F00070301010130260603551D11041F301D820B6273692E62756E642E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443019060767810801010602040E300C02010031071301411302494430350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D0403040381880030818402402C989BD7DEEF12B7D481C45F8B65AB23B1F68414CC56264B043456BA44B54A8817D64039D0D58AEFB8E9AA2AC0ECF0232754218415CDBC48117F24C5F2C207F702404C63A1A79AEA64FFD7FB825B782166B15E45C59441FF5BB267CEF11075D8B8608570494D3B4057B63C0150FDE4C6BB69213A245D8D588A6F13D546EE6E90882F3182013430820130020101304C3046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E7902020550300D06096086480165030402020500A05A301706092A864886F70D010903310A060804007F0007030201303F06092A864886F70D010904313204303D5EE3447468A73FAF8B31A3D2B03239223DE95F55BE9904A03CE8724E779529C825CE660A9C397D9CBB11D88E8D3E5A300A06082A8648CE3D04030304663064023049DC8E15D9A2D87709AA0BD134BDD0FA2F5A541BC88F4DBD7C2B6A34EE9F502CAA0F0767BE761AB2544CB95EF69163D402307CE520A13B8B9145BB0283123E84229E8F09CB0AFBFFC35846679F8C25BE37C83D4D0C728BFF8C7D2BE36D2428CDF3B9");
		persoDataContainer.setEfChipSecurity("3082078C06092A864886F70D010702A082077D30820779020103310F300D0609608648016503040202050030820188060804007F0007030201A082017A04820176318201723012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6CA082049D30820499308203FEA00302010202020550300A06082A8648CE3D0403043046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E79301E170D3234303630333038353334355A170D3335303130333233353935395A305C310B3009060355040613024445310C300A060355040A0C03425349310D300B06035504051304303133313130302E06035504030C275445535420446F63756D656E74205369676E6572204964656E7469747920446F63756D656E7473308201B53082014D06072A8648CE3D020130820140020101303C06072A8648CE3D01010231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B412B1DA197FB71123ACD3A729901D1A71874700133107EC53306404307BC382C63D8C150C3C72080ACE05AFA0C2BEA28E4FB22787139165EFBA91F90F8AA5814A503AD4EB04A8C7DD22CE2826043004A8C7DD22CE28268B39B55416F0447C2FB77DE107DCD2A62E880EA53EEB62D57CB4390295DBC9943AB78696FA504C110461041D1C64F068CF45FFA2A63A81B7C13F6B8847A3E77EF14FE3DB7FCAFE0CBD10E8E826E03436D646AAEF87B2E247D4AF1E8ABE1D7520F9C2A45CB1EB8E95CFD55262B70B29FEEC5864E19C054FF99129280E4646217791811142820341263C53150231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B31F166E6CAC0425A7CF3AB6AF6B7FC3103B883202E904656502010103620004301BB91C96792E65620452749E319C2B62C46B8EBBE846438F098DE4416ECF63463C5518B961482D12FCDFEE2AC349C58B752A893FE94D6E87445976C6309635399E20ED50B5520D501F52835E58AEA0C2A57CC6CFBC7F806228C58F18B0630DA382016630820162301F0603551D23041830168014E4F934EE5ED98D61C3F2EF1A49F290801D08FBB9301D0603551D0E041604149D144787CA96BA8A3325F84BE499114A798CA3C0300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303234303630333038353334355A810F32303235303130333233353935395A30160603551D20040F300D300B060904007F00070301010130260603551D11041F301D820B6273692E62756E642E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443019060767810801010602040E300C02010031071301411302494430350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D0403040381880030818402402C989BD7DEEF12B7D481C45F8B65AB23B1F68414CC56264B043456BA44B54A8817D64039D0D58AEFB8E9AA2AC0ECF0232754218415CDBC48117F24C5F2C207F702404C63A1A79AEA64FFD7FB825B782166B15E45C59441FF5BB267CEF11075D8B8608570494D3B4057B63C0150FDE4C6BB69213A245D8D588A6F13D546EE6E90882F3182013430820130020101304C3046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E7902020550300D06096086480165030402020500A05A301706092A864886F70D010903310A060804007F0007030201303F06092A864886F70D01090431320430BA9B7C4EBFB5500C973FC27B7A997D9D775D7C26C39B449421349F516AB457398EF6D536432760EDCE2C200372BEAF25300A06082A8648CE3D04030304663064023033DFF2B0B128776EDB16A7BB6EE7D5F5FBEC3F9C453CD3135B2400DE92950D05A6B890BF6AF559FC3569018DD9F428FD023044ED0F4CF2AC63FE681FFC0DC444CB5012B02783EB0DB26DA189775169565FE3C29CEA6FEBC20AC6EC0BBACA36393FB1");
		
		String documentNumber = "000000008";
		String sex = "M";
		String mrzLine3 = "GRAF<VON<LYSKY<<KARL<<<<<<<<<<";
		String mrz = persoDataContainer.createMrzFromDgs(documentNumber, sex, mrzLine3);
		
		persoDataContainer.setMrz(mrz);
		persoDataContainer.setEpassDg1PlainData(mrz);
		
		// unprivileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2"),
				HexString.toByteArray("8910074CF4749A916E5864654C768D57F57B6361F70A226DD1AEBED390BB066D")),
				41, false);
		
		// privileged CA key
		persoDataContainer.addCaKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0482ED7BDBBC67FF81507630E88819F3B001E47592D8B686D4C057FB8B75110D2E753F56C2F188337D1BCCA74CD12D7186E1AAD6D8A560DC90D56590BC373E5587"),
				HexString.toByteArray("40055E46C67A76B7BF1A3026400D8C2D9BB243B883E0D150B517120A7651480C")),
				45, true);

		// individual RI key - 1st sector public/private key pair (Sperrmerkmal)
		KeyPair riKeyPair1 = (KeyPair)PersonalizationFactory.unmarshal(new StringReader("""
				<java.security.KeyPair id="1">
				  <privateKey id="2">
				    <algorithm>ECDH</algorithm>
				    <value>3082024B0201003081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101048201553082015102010104203560FBEFA716FF0F0B43E2D606E7EE29363F756B3FC3B71EAED45CAB3C05AD91A081E33081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101A144034200047277546AD6D19858D2026AE8FEB44FF3DB4ECC29050D7613B3D5507C50AC3A724D44D8C34A5F9803FE7FD8286697F1FF073A829E605EF707C85D2BF6571559F0</value>
				  </privateKey>
				  <publicKey id="3">
				    <algorithm>ECDH</algorithm>
				    <value>308201333081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101034200047277546AD6D19858D2026AE8FEB44FF3DB4ECC29050D7613B3D5507C50AC3A724D44D8C34A5F9803FE7FD8286697F1FF073A829E605EF707C85D2BF6571559F0</value>
				  </publicKey>
				</java.security.KeyPair>						
		"""));
		persoDataContainer.addRiKeyPair(riKeyPair1, 1, false);

		// individual RI key - 2nd sector public/private key pair (Pseudonym)
		KeyPair riKeyPair2 = (KeyPair)PersonalizationFactory.unmarshal(new StringReader("""
				<java.security.KeyPair id="1">
				  <privateKey id="2">
				    <algorithm>ECDH</algorithm>
				    <value>3082024B0201003081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A70201010482015530820151020101042081787CAD89A8B58D0D671D44491D0AEDE43F9F905E49C7D7160A1CC8B1371BF1A081E33081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101A1440342000418CD7ED41B38A265F4BC22509F404EA1E2E378F767617816F89195F6446791A11536155464B2F63C395DC40F75D38653304C4DBA764153EE87B9D06AA8392E2F</value>
				  </privateKey>
				  <publicKey id="3">
				    <algorithm>ECDH</algorithm>
				    <value>308201333081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A70201010342000418CD7ED41B38A265F4BC22509F404EA1E2E378F767617816F89195F6446791A11536155464B2F63C395DC40F75D38653304C4DBA764153EE87B9D06AA8392E2F</value>
				  </publicKey>
				</java.security.KeyPair>						
		"""));
		persoDataContainer.addRiKeyPair(riKeyPair2, 2, true);
	}
		
//	@Override
//	protected void addEidDg13(DedicatedFile eIdAppl) {
//		// do not create DG
//	}
	
}
