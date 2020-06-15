package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author amay
 *
 */
public class ProfileUB05 extends AbstractProfile {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = PersonalizationDataContainer.getDefaultContainer();
		persoDataContainer.setDg1PlainData("UB");
		persoDataContainer.setDg3PlainData("20291031");
		persoDataContainer.setDg4PlainData("ÅSA");
		persoDataContainer.setDg5PlainData("LÆRKE");
		persoDataContainer.setDg8PlainData("19950612");
		persoDataContainer.setDg9PlainData("OSLO");
		persoDataContainer.setDg10PlainData("DNK");
		persoDataContainer.setDg13PlainData("BJØRNSON");
		persoDataContainer.setDg17StreetPlainData("AHLSTRÖMINKATU 2");
		persoDataContainer.setDg17CityPlainData("KONTIOLAHTI");
		persoDataContainer.setDg17CountryPlainData("FIN");
		persoDataContainer.setDg17ZipPlainData("81100");
		persoDataContainer.setDg18PlainData("02460000000000");
		persoDataContainer.setEfCardAccess("3181C13012060A04007F0007020204020202010202010D300D060804007F00070202020201023012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C");
		persoDataContainer.setEfCardSecurity("308206B106092A864886F70D010702A08206A23082069E020103310F300D0609608648016503040204050030820188060804007F0007030201A082017A04820176318201723012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F8020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6CA08203EE308203EA30820371A00302010202012D300A06082A8648CE3D0403033055310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369310D300B0603550405130430303033311A301806035504030C115445535420637363612D6765726D616E79301E170D3134303732333036333034305A170D3235303232333233353935395A305C310B3009060355040613024445310C300A060355040A0C03425349310D300B06035504051304303035303130302E06035504030C275445535420446F63756D656E74205369676E6572204964656E7469747920446F63756D656E7473308201133081D406072A8648CE3D02013081C8020101302806072A8648CE3D0101021D00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001303C041CFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE041CB4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4043904B70E0CBD6BB4BF7F321390B94A03C1D356C21122343280D6115C1D21BD376388B5F723FB4C22DFE6CD4375A05A07476444D5819985007E34021D00FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D020101033A00043A79C3CBFDB8A6E569C9226CD54E81DE14381BC92A61AD554EBF349BFAFD72F18DC85D78E49742F37A75411E28E894308D6880D1380FBEB4A382016D30820169301F0603551D23041830168014A38DB7C0DBECF5A91FCA6B3D5EB2F328B5A5DC17301D0603551D0E04160414CF0A2AC150F28ADE4329F662E3D21CE5C78BCDE9300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303134303732333036333034305A810F32303135303232333233353935395A30160603551D20040F300D300B060904007F000703010101302D0603551D1104263024821262756E646573647275636B657265692E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443019060767810801010602040E300C02010031071301411302494430350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D040303036700306402300D90B1C6E52B5E20D8ECE1520981E11EF1AF02906A930420F87E90315588B70C0C9642160E877E42B1CE311849E388B802303450209749C1368D965CE879460F729E68BAB9D5D3269724721D0C564FB2752EC4C0F8F5542990CFDB7C848AA7D0A2BB3182010830820104020101305A3055310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369310D300B0603550405130430303033311A301806035504030C115445535420637363612D6765726D616E7902012D300D06096086480165030402040500A046301706092A864886F70D010903310A060804007F0007030201302B06092A864886F70D010904311E041CC57AFB616E6837B63B22666F48547E3AD71795E33326C0CE5FF27C3A300A06082A8648CE3D0403010440303E021D009AF78546C5467CA4855677B1280A4966CD95E7E00554778249502C3E021D00BFA6AC8C55834446E1B436747680F2E43B22D3D2733EB7352C0956DE");
		persoDataContainer.setEfChipSecurity("308208DD06092A864886F70D010702A08208CE308208CA020103310F300D06096086480165030402040500308203B5060804007F0007030201A08203A7048203A33182039F3012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101FF3012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000419D4B7447788B0E1993DB35500999627E739A4E5E35F02D8FB07D6122E76567F17758D7A3AA6943EF23E5E2909B3E8B31BFAA4544C2CBF1FB487F31FF239C8F80201293081A3060804007F00070202083181963012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D3062060904007F0007020201023052300C060704007F0007010202010D03420004A7A6034366A5AC38533FEF2D8D70E958E50D7152389054DBDF8922D5D7003C81A03EB8E32FF5BD640F20771056FE042AE4446B7B2B4CCD92BC0A9FBE168DBCA502012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C308201C3060804007F0007020207308201B5300B0609608648016503040204308201A43021020101041C2FF0247F59DD3C646E314F03ABB33EE91A586577EBDF48D3864EC34D3021020102041C37823963B71AF0BF5698D1FDC30DA2B7F9ECE57CFA4959BEE9D6D9943021020103041CA105E4EF19FEEC01DC64FBE1AECBEBC2A492DE78C89D439A8C301E853021020104041CB3BFE02651A100AAA3538E4A1DB32D39848C5D703D5CBED28449221B3021020105041CA90F28EB7A0FA0DE83ABF3293D14E0838B9C85FC7277CBB97737A32B3021020106041C712B8550E49A13C64DCED4457E9A0F5A85DC26CD6A321596723005D63021020107041C42A8FA36B60887ED022CD3B6ECC255220FBE8CB3F607E416601FCAA63021020108041C035373E42D8B71E62E5D0DE2799B93933680EB160B9AC93BA5E7C4BF3021020109041C236A93065EA42769EB04F50FD21A2EE89E4B6EE87D217FEA2EF028C6302102010D041C859FE631F5DA379D44239EB85FAFDF7D52FDBC88986B254045DCF82A3021020111041CA2E3B164CFE1192D74C67A55904D954964D00E55C56E1AC4788E9E173021020112041C3E2D07D3EDAE3EE0829A2CAAB0A37B08BC410FB90A6782F09439E380A08203EE308203EA30820371A00302010202012D300A06082A8648CE3D0403033055310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369310D300B0603550405130430303033311A301806035504030C115445535420637363612D6765726D616E79301E170D3134303732333036333034305A170D3235303232333233353935395A305C310B3009060355040613024445310C300A060355040A0C03425349310D300B06035504051304303035303130302E06035504030C275445535420446F63756D656E74205369676E6572204964656E7469747920446F63756D656E7473308201133081D406072A8648CE3D02013081C8020101302806072A8648CE3D0101021D00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001303C041CFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE041CB4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4043904B70E0CBD6BB4BF7F321390B94A03C1D356C21122343280D6115C1D21BD376388B5F723FB4C22DFE6CD4375A05A07476444D5819985007E34021D00FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D020101033A00043A79C3CBFDB8A6E569C9226CD54E81DE14381BC92A61AD554EBF349BFAFD72F18DC85D78E49742F37A75411E28E894308D6880D1380FBEB4A382016D30820169301F0603551D23041830168014A38DB7C0DBECF5A91FCA6B3D5EB2F328B5A5DC17301D0603551D0E04160414CF0A2AC150F28ADE4329F662E3D21CE5C78BCDE9300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303134303732333036333034305A810F32303135303232333233353935395A30160603551D20040F300D300B060904007F000703010101302D0603551D1104263024821262756E646573647275636B657265692E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443019060767810801010602040E300C02010031071301411302494430350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D040303036700306402300D90B1C6E52B5E20D8ECE1520981E11EF1AF02906A930420F87E90315588B70C0C9642160E877E42B1CE311849E388B802303450209749C1368D965CE879460F729E68BAB9D5D3269724721D0C564FB2752EC4C0F8F5542990CFDB7C848AA7D0A2BB3182010730820103020101305A3055310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369310D300B0603550405130430303033311A301806035504030C115445535420637363612D6765726D616E7902012D300D06096086480165030402040500A046301706092A864886F70D010903310A060804007F0007030201302B06092A864886F70D010904311E041C02BE3CDDAC9D8F1AD4C96243ABDC69057236C59B380BF9B02EDD5DD6300A06082A8648CE3D040301043F303D021C604560A1FA541FC5976CC952F62D9C4276C754121033D1B94B57E0ED021D00EE47FA4ADFCBBD7EF5DA051EE0CA5C799EDAB4D9D1FC9F81D6752C6E");
		
		String documentNumber = "00000UB05";
		String sex = "F";
		String mrzLine3 = "LAERKE<<ASA<<<<<<<<<<<<<<<<<<<";
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
				HexString.toByteArray("0437ABECCB38B1799E9BAEF4C0C55CF6F3F1839277107AD41EC29FEF2BC4BE184A0CF2104EE78D5285679E257713D703378C048A6EFCA55914695D5F92D6736106"),
				HexString.toByteArray("715EB2BFA96B66D222828BFDEC47DB4DCBF88644BEA7ECEC1E0F5072DB278475")),
				45, true);
		
		// individual RI key - 1st sector public/private key pair (Sperrmerkmal)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("044BF18FB3F2496D3E1260B9ACC730612A0FB08D7FAAE82580EB497ACB2D6EB44097D7349A775B1E54ACEAADD0AB06D63FE04C79146BFEEBA61E6E6A48C1BC12D7"),
				HexString.toByteArray("2383500E5951B1CD6B60DBC933EEED9876714F933DB52DABE49536151C2399C1")),
				1, false);

		// individual RI key - 2nd sector public/private key pair (Pseudonym)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("047EB6F627D359596460597A699B7FA90D558FBDDB88E7F72AA48DFDBD0037BEEC8D5CC87AF0EF47E2F80A4E9689930CB41911785A3988C9DDBFFF5E88921C4EF7"),
				HexString.toByteArray("61F6976931DC37F09AC70F0D0276A45D359E69D6EFF64564D045514B08409013")),
				2, true);
	}

}