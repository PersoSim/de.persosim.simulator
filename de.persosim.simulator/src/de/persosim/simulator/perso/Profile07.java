package de.persosim.simulator.perso;

import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.utils.HexString;

/**
 * @author slutters
 *
 */
public class Profile07 extends AbstractProfile {
	
	@Override
	public void setPersoDataContainer() {
		persoDataContainer = PersonalizationDataContainer.getDefaultContainer();
		persoDataContainer.setDg4PlainData("ANNEKATHRIN");
		persoDataContainer.setDg5PlainData("LERCH");
		persoDataContainer.setDg8PlainData("19760705");
		persoDataContainer.setDg9PlainData("BAD KÖNIGSHOFEN I. GRABFELD");
		persoDataContainer.setDg13PlainData("BJØRNSON");
		persoDataContainer.setDg17CityPlainData("HALLE (SAALE)");
		persoDataContainer.setDg17CountryPlainData("D");
		persoDataContainer.setDg17ZipPlainData("06108");
		persoDataContainer.setDg18PlainData("02760200000000");
		persoDataContainer.setEfCardAccess("3181C13012060A04007F0007020204020202010202010D300D060804007F00070202020201023012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C");
		persoDataContainer.setEfCardSecurity("308206AF06092A864886F70D010702A08206A03082069C020103310F300D0609608648016503040204050030820188060804007F0007030201A082017A04820176318201723012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101013012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC2020129303E060804007F000702020831323012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6CA08203EE308203EA30820371A00302010202012D300A06082A8648CE3D0403033055310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369310D300B0603550405130430303033311A301806035504030C115445535420637363612D6765726D616E79301E170D3134303732333036333034305A170D3235303232333233353935395A305C310B3009060355040613024445310C300A060355040A0C03425349310D300B06035504051304303035303130302E06035504030C275445535420446F63756D656E74205369676E6572204964656E7469747920446F63756D656E7473308201133081D406072A8648CE3D02013081C8020101302806072A8648CE3D0101021D00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001303C041CFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE041CB4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4043904B70E0CBD6BB4BF7F321390B94A03C1D356C21122343280D6115C1D21BD376388B5F723FB4C22DFE6CD4375A05A07476444D5819985007E34021D00FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D020101033A00043A79C3CBFDB8A6E569C9226CD54E81DE14381BC92A61AD554EBF349BFAFD72F18DC85D78E49742F37A75411E28E894308D6880D1380FBEB4A382016D30820169301F0603551D23041830168014A38DB7C0DBECF5A91FCA6B3D5EB2F328B5A5DC17301D0603551D0E04160414CF0A2AC150F28ADE4329F662E3D21CE5C78BCDE9300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303134303732333036333034305A810F32303135303232333233353935395A30160603551D20040F300D300B060904007F000703010101302D0603551D1104263024821262756E646573647275636B657265692E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443019060767810801010602040E300C02010031071301411302494430350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D040303036700306402300D90B1C6E52B5E20D8ECE1520981E11EF1AF02906A930420F87E90315588B70C0C9642160E877E42B1CE311849E388B802303450209749C1368D965CE879460F729E68BAB9D5D3269724721D0C564FB2752EC4C0F8F5542990CFDB7C848AA7D0A2BB3182010630820102020101305A3055310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369310D300B0603550405130430303033311A301806035504030C115445535420637363612D6765726D616E7902012D300D06096086480165030402040500A046301706092A864886F70D010903310A060804007F0007030201302B06092A864886F70D010904311E041C2509E9A2F5096EF768FFC5010F9E7A4754B72D0C43076593D2F94308300A06082A8648CE3D040301043E303C021C5471C8B7F4D39818CF4F734AC211E07952886DBDE90B1E4E58CE8606021C1786E1E4C4B842A169C5B41A333ECC50CE9EE61AF13094689290E99D");
		persoDataContainer.setEfChipSecurity("308208DD06092A864886F70D010702A08208CE308208CA020103310F300D06096086480165030402040500308203B5060804007F0007030201A08203A7048203A33182039F3012060A04007F0007020204020202010202010D300D060804007F00070202020201023017060A04007F0007020205020330090201010201010101003019060904007F000702020502300C060704007F0007010202010D3017060A04007F0007020205020330090201010201020101013012060A04007F00070202030202020102020129301C060904007F000702020302300C060704007F0007010202010D0201293062060904007F0007020201023052300C060704007F0007010202010D0342000467DBFBD14C3291267FEFF537062570B96BE2274D7747D734BBDB5BFEAAD0976C3E47B929F42B1FCD583F80FB469225E29FE00AC6C95C24E956CB8E7031C19AC20201293081A3060804007F00070202083181963012060A04007F0007020203020202010202012D301C060904007F000702020302300C060704007F0007010202010D02012D3062060904007F0007020201023052300C060704007F0007010202010D034200043F8F473E895EF19EE2482771496C922468E15509A1A83610CC4F9D0E46E3170846B967928F18A46F7B4F8FF6D464E97E3ADD7324253BEE0521A57D2138060C6302012D302A060804007F0007020206161E687474703A2F2F6273692E62756E642E64652F6369662F6E70612E786D6C308201C3060804007F0007020207308201B5300B0609608648016503040204308201A43021020101041C2FF0247F59DD3C646E314F03ABB33EE91A586577EBDF48D3864EC34D3021020102041C37823963B71AF0BF5698D1FDC30DA2B7F9ECE57CFA4959BEE9D6D9943021020103041CA105E4EF19FEEC01DC64FBE1AECBEBC2A492DE78C89D439A8C301E853021020104041CCEECFDD547214EE9FFC6450BCCEFADACE064E6DB759A64F157610B643021020105041CA55083EEC3155FA0513812CDF0D0124737D00578F200B761FAB62F393021020106041C712B8550E49A13C64DCED4457E9A0F5A85DC26CD6A321596723005D63021020107041C42A8FA36B60887ED022CD3B6ECC255220FBE8CB3F607E416601FCAA63021020108041C92C24CBD7DAAAAE2BB49D625E250E1D3900B40F3E2745427D936DE873021020109041CF56014061AE757CA3BE8DAC1F6B0B585636B1F2C5A0485F71CABA635302102010D041C9FB5FC5B4DC81A3CDC8505280D5FE5815E65CF66EBE440594608E3A33021020111041C2577FD4280E8BB5E3CCB457D274DBCDF336894317DFB3F7B790FE2EE3021020112041CC203D1D96EDF22B34B906EBDD9FD4C84B80C8113BE8D631FE7EA223AA08203EE308203EA30820371A00302010202012D300A06082A8648CE3D0403033055310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369310D300B0603550405130430303033311A301806035504030C115445535420637363612D6765726D616E79301E170D3134303732333036333034305A170D3235303232333233353935395A305C310B3009060355040613024445310C300A060355040A0C03425349310D300B06035504051304303035303130302E06035504030C275445535420446F63756D656E74205369676E6572204964656E7469747920446F63756D656E7473308201133081D406072A8648CE3D02013081C8020101302806072A8648CE3D0101021D00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF000000000000000000000001303C041CFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFE041CB4050A850C04B3ABF54132565044B0B7D7BFD8BA270B39432355FFB4043904B70E0CBD6BB4BF7F321390B94A03C1D356C21122343280D6115C1D21BD376388B5F723FB4C22DFE6CD4375A05A07476444D5819985007E34021D00FFFFFFFFFFFFFFFFFFFFFFFFFFFF16A2E0B8F03E13DD29455C5C2A3D020101033A00043A79C3CBFDB8A6E569C9226CD54E81DE14381BC92A61AD554EBF349BFAFD72F18DC85D78E49742F37A75411E28E894308D6880D1380FBEB4A382016D30820169301F0603551D23041830168014A38DB7C0DBECF5A91FCA6B3D5EB2F328B5A5DC17301D0603551D0E04160414CF0A2AC150F28ADE4329F662E3D21CE5C78BCDE9300E0603551D0F0101FF040403020780302B0603551D1004243022800F32303134303732333036333034305A810F32303135303232333233353935395A30160603551D20040F300D300B060904007F000703010101302D0603551D1104263024821262756E646573647275636B657265692E6465A40E300C310A300806035504070C014430510603551D12044A30488118637363612D6765726D616E79406273692E62756E642E6465861C68747470733A2F2F7777772E6273692E62756E642E64652F63736361A40E300C310A300806035504070C01443019060767810801010602040E300C02010031071301411302494430350603551D1F042E302C302AA028A0268624687474703A2F2F7777772E6273692E62756E642E64652F746573745F637363615F63726C300A06082A8648CE3D040303036700306402300D90B1C6E52B5E20D8ECE1520981E11EF1AF02906A930420F87E90315588B70C0C9642160E877E42B1CE311849E388B802303450209749C1368D965CE879460F729E68BAB9D5D3269724721D0C564FB2752EC4C0F8F5542990CFDB7C848AA7D0A2BB3182010730820103020101305A3055310B3009060355040613024445310D300B060355040A0C0462756E64310C300A060355040B0C03627369310D300B0603550405130430303033311A301806035504030C115445535420637363612D6765726D616E7902012D300D06096086480165030402040500A046301706092A864886F70D010903310A060804007F0007030201302B06092A864886F70D010904311E041CE98D9E17744A36DFB2D77A87D01B4B67695374343C98AD380C88CD55300A06082A8648CE3D040301043F303D021C2AABD8063723BB325782E71D8A793D6F7A0110C2831C53BF90578332021D00817DA7B629F6D14D9C9049BCB2B051790B8B85CD9E18B9B4B2DB69D8");
		
		String documentNumber = "000000007";
		String sex = "F";
		String mrzLine3 = "LERCH<<ANNEKATHRIN<<<<<<<<<<<<";
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
				HexString.toByteArray("043F8F473E895EF19EE2482771496C922468E15509A1A83610CC4F9D0E46E3170846B967928F18A46F7B4F8FF6D464E97E3ADD7324253BEE0521A57D2138060C63"),
				HexString.toByteArray("6777611DD39BCC83ADE6495F4F9C2379B7FEE67D662F470E32853186AE176EEF")),
				45, true);
		
		// individual RI key - 1st sector public/private key pair (Sperrmerkmal)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("043CAE3E81FE93B9404191A398C3D51CFA13938ACA6AD413A6F8AF07201277ACAA3C0F925C68ED2DCEFB9C8389A569FEA7E40D7F411758190FA729D570399AF76E"),
				HexString.toByteArray("1876476346AF80B6F097AE98CA4D97DE41F3F1953C2E458F53F30E1EC5563B97")),
				1, false);

		// individual RI key - 2nd sector public/private key pair (Pseudonym)
		persoDataContainer.addRiKeyPair(CryptoUtil.reconstructKeyPair(13,
				HexString.toByteArray("0457BB80902D94AE7243F0737421C21D1C30D238F4F21A22FC14EED18D3BE5191035DC471939D887E376A925FA8AEFFFA97F3553EA84844B725EA3B1393ACB3E95"),
				HexString.toByteArray("0B485F3D9D674B2A1F25ED6FA1E54392D57389358535DA57D6C0DFFA3649C6E6")),
				2, true);
	}
	
}
