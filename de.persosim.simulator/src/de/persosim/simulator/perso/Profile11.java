package de.persosim.simulator.perso;

import java.io.StringReader;
import java.security.KeyPair;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

public class Profile11 extends AbstractProfile {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = PersonalizationDataContainer.getDefaultContainer();
//		persoDataContainer.setDg3PlainData("20191231");
		persoDataContainer.setDg3PlainData("20240630");
		persoDataContainer.setDg4PlainData("HILDEGARD");
		persoDataContainer.setDg5PlainData("MÜLLER");
		persoDataContainer.setDg8PlainData("19590204");
		persoDataContainer.setDg9PlainData("SAARBRÜCKEN");
		persoDataContainer.setDg10PlainData("D");
		persoDataContainer.setDg17StreetPlainData("HARKORTSTR. 58");
		persoDataContainer.setDg17CityPlainData("DORTMUND");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("44225");
		persoDataContainer.setDg18PlainData("02760509130000");
		persoDataContainer.setEfCardAccess("3181C13012060A04007F0007020204020202010202010D300D060804007F00070202020201023012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C");
		persoDataContainer.setEfCardSecurity("308209DC06092A864886F70D010702A08209CD308209C9020103310F300D06096086480165030402020500308203D8060804007F0007030201A08203CA048203C6318203C23012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC20201293081A3060804007F00070202083181963012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D3062060904007F0007020201023052300C060704007F0007010202010D0342000465F192A4270BB47C0C05BB4FDE29DB38031082F0DD830D533C3D0EB52F31F1249E6773E2202C7E168A20A1F2CAD4ED61766C697C3E3533916ADE402B412DAEA502012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C308201E6060804007F0007020207308201D8300B0609608648016503040204308201C73021020101041C2FF0247F59DD3C646E314F03ABB33EE91A586577EBDF48D3864EC34D3021020102041C37823963B71AF0BF5698D1FDC30DA2B7F9ECE57CFA4959BEE9D6D9943021020103041C72A52EC02865C477A48E42273B5ED3FEAD9F75072B7D7360025D8D773021020104041CAF3C7C9212A849D3EEDA77A47740778C6C4422068B9CC5429939165E3021020105041C244ED99256072BB0A92993264BB994D37F3691A1D49009E47FF1683F3021020106041C712B8550E49A13C64DCED4457E9A0F5A85DC26CD6A321596723005D63021020107041C42A8FA36B60887ED022CD3B6ECC255220FBE8CB3F607E416601FCAA63021020108041C508EB20B0AE930630A2C74B3001037BD2571D08D99FB6FAAD37B54793021020109041CC56C3CF4D28EA2A2617FEE65BDDF015B41BA2BA6A4BAFA948E0C400D302102010A041C1880A259CDB497C15A7FDD1C9AC9490D7DC0D18743378603D43D1D4F302102010D041C859FE631F5DA379D44239EB85FAFDF7D52FDBC88986B254045DCF82A3021020111041C4F585AC7F3E48597EF6421C34EE2C3718D67799DD30DCC917CC751983021020112041C52EC6D4D4361E1C1978FB20837A9CADF44A4951DB48D80B6AB15B361A082049D30820499308203FEA00302010202020550300A06082A8648CE3D0403043046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E79301E170D3234303630333038353334355A170D3335303130333233353935395A305C310B3009060355040613024445310C300A060355040A0C03425349310D300B06035504051304303133313130302E06035504030C275445535420446F63756D656E74205369676E6572204964656E7469747920446F63756D656E7473308201B53082014D06072A8648CE3D020130820140020101303C06072A8648CE3D01010231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B412B1DA197FB71123ACD3A729901D1A71874700133107EC53306404307BC382C63D8C150C3C72080ACE05AFA0C2BEA28E4FB22787139165EFBA91F90F8AA5814A503AD4EB04A8C7DD22CE2826043004A8C7DD22CE28268B39B55416F0447C2FB77DE107DCD2A62E880EA53EEB62D57CB4390295DBC9943AB78696FA504C110461041D1C64F068CF45FFA2A63A81B7C13F6B8847A3E77EF14FE3DB7FCAFE0CBD10E8E826E03436D646AAEF87B2E247D4AF1E8ABE1D7520F9C2A45CB1EB8E95CFD55262B70B29FEEC5864E19C054FF99129280E4646217791811142820341263C53150231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B31F166E6CAC0425A7CF3AB6AF6B7FC3103B883202E904656502010103620004301BB91C96792E65620452749E319C2B62C46B8EBBE846438F098DE4416ECF63463C5518B961482D12FCDFEE2AC349C58B752A893FE94D6E87445976C6309635399E20ED50B5520D501F52835E58AEA0C2A57CC6CFBC7F806228C58F18B0630DA382016630820162301F0603551D23041830168014E4F934EE5ED98D61C3F2EF1A49F290801D08FBB9301D0603551D0E041604149D144787CA96BA8A3325F84BE499114A798CA3C0300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303234303630333038353334355A810F32303235303130333233353935395A30160603551D20040F300D300B060904007F00070301010130260603551D11041F301D820B6273692E62756E642E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443019060767810801010602040E300C02010031071301411302494430350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D0403040381880030818402402C989BD7DEEF12B7D481C45F8B65AB23B1F68414CC56264B043456BA44B54A8817D64039D0D58AEFB8E9AA2AC0ECF0232754218415CDBC48117F24C5F2C207F702404C63A1A79AEA64FFD7FB825B782166B15E45C59441FF5BB267CEF11075D8B8608570494D3B4057B63C0150FDE4C6BB69213A245D8D588A6F13D546EE6E90882F3182013430820130020101304C3046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E7902020550300D06096086480165030402020500A05A301706092A864886F70D010903310A060804007F0007030201303F06092A864886F70D010904313204308A7269F47793B22C2D147D1AA631F807CCDA9C5F288A7E72BD1AC39D670432DF5DDB5BA234845F3C6CFB3354F115517F300A06082A8648CE3D04030304663064023048FBE29DD06A866C8985B099F9413ABC7E81173B862F804CB36684E0D1C9702D6FB491F19ACF98E97F182003F316B89F023045FAE882CEEFF72A45B5EDBAB036C6AB3E1D71D9BA203726429E7B1FDA62255CDA9B43E288DBC591B1521E5DC456FD7A");
		persoDataContainer.setEfChipSecurity("3082078C06092A864886F70D010702A082077D30820779020103310F300D0609608648016503040202050030820188060804007F0007030201A082017A04820176318201723012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6CA082049D30820499308203FEA00302010202020550300A06082A8648CE3D0403043046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E79301E170D3234303630333038353334355A170D3335303130333233353935395A305C310B3009060355040613024445310C300A060355040A0C03425349310D300B06035504051304303133313130302E06035504030C275445535420446F63756D656E74205369676E6572204964656E7469747920446F63756D656E7473308201B53082014D06072A8648CE3D020130820140020101303C06072A8648CE3D01010231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B412B1DA197FB71123ACD3A729901D1A71874700133107EC53306404307BC382C63D8C150C3C72080ACE05AFA0C2BEA28E4FB22787139165EFBA91F90F8AA5814A503AD4EB04A8C7DD22CE2826043004A8C7DD22CE28268B39B55416F0447C2FB77DE107DCD2A62E880EA53EEB62D57CB4390295DBC9943AB78696FA504C110461041D1C64F068CF45FFA2A63A81B7C13F6B8847A3E77EF14FE3DB7FCAFE0CBD10E8E826E03436D646AAEF87B2E247D4AF1E8ABE1D7520F9C2A45CB1EB8E95CFD55262B70B29FEEC5864E19C054FF99129280E4646217791811142820341263C53150231008CB91E82A3386D280F5D6F7E50E641DF152F7109ED5456B31F166E6CAC0425A7CF3AB6AF6B7FC3103B883202E904656502010103620004301BB91C96792E65620452749E319C2B62C46B8EBBE846438F098DE4416ECF63463C5518B961482D12FCDFEE2AC349C58B752A893FE94D6E87445976C6309635399E20ED50B5520D501F52835E58AEA0C2A57CC6CFBC7F806228C58F18B0630DA382016630820162301F0603551D23041830168014E4F934EE5ED98D61C3F2EF1A49F290801D08FBB9301D0603551D0E041604149D144787CA96BA8A3325F84BE499114A798CA3C0300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303234303630333038353334355A810F32303235303130333233353935395A30160603551D20040F300D300B060904007F00070301010130260603551D11041F301D820B6273692E62756E642E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443019060767810801010602040E300C02010031071301411302494430350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D0403040381880030818402402C989BD7DEEF12B7D481C45F8B65AB23B1F68414CC56264B043456BA44B54A8817D64039D0D58AEFB8E9AA2AC0ECF0232754218415CDBC48117F24C5F2C207F702404C63A1A79AEA64FFD7FB825B782166B15E45C59441FF5BB267CEF11075D8B8608570494D3B4057B63C0150FDE4C6BB69213A245D8D588A6F13D546EE6E90882F3182013430820130020101304C3046310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369311A301806035504030C115445535420637363612D6765726D616E7902020550300D06096086480165030402020500A05A301706092A864886F70D010903310A060804007F0007030201303F06092A864886F70D01090431320430BA9B7C4EBFB5500C973FC27B7A997D9D775D7C26C39B449421349F516AB457398EF6D536432760EDCE2C200372BEAF25300A06082A8648CE3D04030304663064023070C3EC0E9DF131A127A3DC87A678E05D9CC874D65ABC2C5098B9AB9CF133EF2E401EF7C80DF3062A0469F6C5E29FC0C9023073B0A214B2B0781DC0F830749E0020298A157593B791A262F9DC5D681FC97F8504A0E48E6AF734A0B79B0D345534F375");
		
		String documentNumber = "000000010";
		String sex = "F";
		String mrzLine3 = "MUELLER<<HILDEGARD<<<<<<<<<<<<";
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
				HexString.toByteArray("0465F192A4270BB47C0C05BB4FDE29DB38031082F0DD830D533C3D0EB52F31F1249E6773E2202C7E168A20A1F2CAD4ED61766C697C3E3533916ADE402B412DAEA5"),
				HexString.toByteArray("46A957BAE8EA7D99183CAC13345CE667EC2F76D70E0095CE15D01F2686C3BD64")),
				45, true);

		// individual RI key - 1st sector public/private key pair (Sperrmerkmal)
		KeyPair riKeyPair1 = (KeyPair)PersonalizationFactory.unmarshal(new StringReader("""
				<java.security.KeyPair id="1">
				  <privateKey id="2">
				    <algorithm>ECDH</algorithm>
				    <value>3082024B0201003081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A70201010482015530820151020101042085624E604FC1E57BC28183DE486672AEB676325C91683BB2962AABB7525E03C3A081E33081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101A144034200048F3065B5EEE71C43F95B3F13A746E370E8EFE59BAFFDAB9FCB42109252D8B1587E9ABC406012B6641BDF45BB6054146AF585544B207CA49154930CEA6EABAD56</value>
				  </privateKey>
				  <publicKey id="3">
				    <algorithm>ECDH</algorithm>
				    <value>308201333081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101034200048F3065B5EEE71C43F95B3F13A746E370E8EFE59BAFFDAB9FCB42109252D8B1587E9ABC406012B6641BDF45BB6054146AF585544B207CA49154930CEA6EABAD56</value>
				  </publicKey>
				</java.security.KeyPair>						
		"""));
		persoDataContainer.addRiKeyPair(riKeyPair1, 1, false);

		// individual RI key - 2nd sector public/private key pair (Pseudonym)
		KeyPair riKeyPair2 = (KeyPair)PersonalizationFactory.unmarshal(new StringReader("""
				<java.security.KeyPair id="1">
				  <privateKey id="2">
				    <algorithm>ECDH</algorithm>
				    <value>3082024B0201003081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A70201010482015530820151020101042057A30ECE6F4148DC0F4DC57FF0F594668174B808271677C7EC7290BB9DCFA5FBA081E33081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A7020101A14403420004044BB09A2DF81A4851903AD28BC03BF57EE38FBF1C7D56362D85083BC34BB5507139F1CFCB1E065E4C6BDE52196C37844EAD229E610C780ACA7CB3601CB8091B</value>
				  </privateKey>
				  <publicKey id="3">
				    <algorithm>ECDH</algorithm>
				    <value>308201333081EC06072A8648CE3D02013081E0020101302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377304404207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9042026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B60441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F046997022100A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A702010103420004044BB09A2DF81A4851903AD28BC03BF57EE38FBF1C7D56362D85083BC34BB5507139F1CFCB1E065E4C6BDE52196C37844EAD229E610C780ACA7CB3601CB8091B</value>
				  </publicKey>
				</java.security.KeyPair>						
		"""));
		persoDataContainer.addRiKeyPair(riKeyPair2, 2, true);
	}

}
