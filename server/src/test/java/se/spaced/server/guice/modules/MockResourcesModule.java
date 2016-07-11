package se.spaced.server.guice.modules;

import com.google.common.io.ByteSource;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import se.fearless.common.io.IOLocator;
import se.fearless.common.mock.MockUtil;
import se.mockachino.Mockachino;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MockResourcesModule implements Module {

	@Override
	public void configure(Binder binder) {
	}

	@Provides
	@Singleton
	public IOLocator getStreamLocator() {
		IOLocator streamLocator = MockUtil.deepMock(IOLocator.class);
		setupPolygonGraphMockData(streamLocator);


		return streamLocator;
	}

	private void setupPolygonGraphMockData(IOLocator streamLocator) {

		Mockachino.stubReturn(supplier(getPolyGraphData())).on(streamLocator).getByteSource("mobs/navmesh/fearless.xml");
		Mockachino.stubReturn(supplier(getPolyGraphData())).on(streamLocator).getByteSource(
				"/mobs/navmesh/fearless.xml");
		Mockachino.stubReturn(supplier(getOuterZoneWithNoChildren())).on(streamLocator).getByteSource(
				"/zone/spacebattle/outerSpace.zone");
		Mockachino.stubReturn(supplier(getTheStormXmo())).on(streamLocator).getByteSource(
				"/props/world/buildings/the_storm.xmo");
	}

	private ByteSource supplier(final byte[] content) {
		return new ByteSource() {
			@Override
			public InputStream openStream() throws IOException {
				return new ByteArrayInputStream(content);
			}
		};
	}

	private byte[] getTheStormXmo() {
		String data = "<se.spaced.shared.model.xmo.XmoRoot>\n" +
				"\t<size>\n" +
				"\t\t<double>1.0</double>\n" +
				"\t\t<double>1.0</double>\n" +
				"\t\t<double>1.0</double>\n" +
				"\t</size>\n" +
				"\t<extendedMeshObjects>\n" +
				"\t\t<se.spaced.shared.model.xmo.ExtendedMeshObject>\n" +
				"\t\t\t<xmoMaterialFile>materials/default.material</xmoMaterialFile>\n" +
				"\t\t\t<physicsFile>static/collada/vehicles/the_storm_hull.DAE</physicsFile>\n" +
				"\t\t\t<colladaFile>static/collada/vehicles/the_storm_hull.DAE</colladaFile>\n" +
				"\t\t\t<textureFile>textures/props/vehicles/Atlas.png</textureFile>\n" +
				"\t\t\t<position>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t</position>\n" +
				"\t\t\t<rotation>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t\t<double>1.0</double>\n" +
				"\t\t\t</rotation>\n" +
				"\t\t\t<scale>\n" +
				"\t\t\t\t<double>1.0</double>\n" +
				"\t\t\t\t<double>1.0</double>\n" +
				"\t\t\t\t<double>1.0</double>\n" +
				"\t\t\t</scale>\n" +
				"\t\t</se.spaced.shared.model.xmo.ExtendedMeshObject>\n" +
				"\t\t<se.spaced.shared.model.xmo.ExtendedMeshObject>\n" +
				"\t\t\t<xmoMaterialFile>materials/emissive.material</xmoMaterialFile>\n" +
				"\t\t\t<colladaFile>static/collada/vehicles/the_storm_emissive.DAE</colladaFile>\n" +
				"\t\t\t<physicsFile>static/collada/vehicles/the_storm_emissive.DAE</physicsFile>\n" +
				"\t\t\t<textureFile>textures/props/vehicles/Atlas.png</textureFile>\n" +
				"\t\t\t<position>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t</position>\n" +
				"\t\t\t<rotation>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t\t<double>0.0</double>\n" +
				"\t\t\t\t<double>1.0</double>\n" +
				"\t\t\t</rotation>\n" +
				"\t\t\t<scale>\n" +
				"\t\t\t\t<double>1.0</double>\n" +
				"\t\t\t\t<double>1.0</double>\n" +
				"\t\t\t\t<double>1.0</double>\n" +
				"\t\t\t</scale>\n" +
				"\t\t</se.spaced.shared.model.xmo.ExtendedMeshObject>\n" +
				"\t</extendedMeshObjects>\n" +
				"\t<walkmeshFile>mobs/navmesh/fearless.xml</walkmeshFile>\n" +
				"</se.spaced.shared.model.xmo.XmoRoot>\n" +
				"\n";
		return data.getBytes();
	}

	private byte[] getOuterZoneWithNoChildren() {
		String data = "<se.spaced.shared.resources.zone.Zone>\n" +
				"  <name>outerSpace</name>\n" +
				"  <shape class=\"Sphere\" radius=\"120000.0\">\n" +
				"    <center x=\"0.0\" y=\"0.0\" z=\"0.0\"/>\n" +
				"  </shape>\n" +
				"  <props>\n" +
				"    <se.spaced.client.model.Prop>\n" +
				"      <location x=\"800.0\" y=\"600.0\" z=\"-3100.0\"/>\n" +
				"      <rotation x=\"2.4920011842825406E-4\" y=\"0.30648853373904417\" z=\"-8.023851233349423E-5\" w=\"0.9518743142598046\"/>\n" +
				"      <scale x=\"1.0\" y=\"1.0\" z=\"1.0\"/>\n" +
				"      <xmoFile>/props/world/buildings/the_storm.xmo</xmoFile>\n" +
				"    </se.spaced.client.model.Prop>\n" +
				"  </props>\n" +
				"  <environmentDayCycleSettingsFile>environment/outerspace.env</environmentDayCycleSettingsFile>\n" +
				"  <envMaxWeight>1.0</envMaxWeight>\n" +
				"  <envOuterFadeDistance>100.0</envOuterFadeDistance>\n" +
				"  <envInnerFadeDistance>15.0</envInnerFadeDistance>\n" +
				"  <subzoneFiles/>\n" +
				"</se.spaced.shared.resources.zone.Zone>";
		return data.getBytes();
	}

	private byte[] getPolyGraphData() {
		String data = "<walkmesh>\n" +
				"\t<polygons>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"42.68735885620117\" y=\"-37.41884386539459\" z=\"-98.46235656738281\"/>\n" +
				"\t\t\t\t<vector3 x=\"52.296348571777344\" y=\"-37.42738878726959\" z=\"-85.47578430175781\"/>\n" +
				"\t\t\t\t<vector3 x=\"39.769412994384766\" y=\"-37.42752993106842\" z=\"-80.98175811767578\"/>\n" +
				"\t\t\t\t<vector3 x=\"34.35565948486328\" y=\"-37.4252792596817\" z=\"-87.86817932128906\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>860ba91f-a298-4897-847f-a014051bc8f7</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"39.769412994384766\" y=\"-37.42752993106842\" z=\"-80.98175811767578\"/>\n" +
				"\t\t\t\t<vector3 x=\"34.35565948486328\" y=\"-37.4252792596817\" z=\"-87.86817932128906\"/>\n" +
				"\t\t\t\t<vector3 x=\"-1.770926594734192\" y=\"-37.4385849237442\" z=\"-61.91138458251953\"/>\n" +
				"\t\t\t\t<vector3 x=\"10.407261848449707\" y=\"-37.45839846134186\" z=\"-59.860774993896484\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>fcb41f40-5cda-4bc1-bb9c-a014051bd21a</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"10.407261848449707\" y=\"-37.45839846134186\" z=\"-59.860774993896484\"/>\n" +
				"\t\t\t\t<vector3 x=\"-1.770926594734192\" y=\"-37.4385849237442\" z=\"-61.91138458251953\"/>\n" +
				"\t\t\t\t<vector3 x=\"4.322529315948486\" y=\"-37.33474123477936\" z=\"-53.440635681152344\"/>\n" +
				"\t\t\t\t<vector3 x=\"11.363594055175781\" y=\"-37.285714745521545\" z=\"-58.383304595947266\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>2aa76c06-8f0e-4d98-9837-a014051bd732</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"4.322529315948486\" y=\"-37.33474123477936\" z=\"-53.440635681152344\"/>\n" +
				"\t\t\t\t<vector3 x=\"11.363594055175781\" y=\"-37.285714745521545\" z=\"-58.383304595947266\"/>\n" +
				"\t\t\t\t<vector3 x=\"16.283199310302734\" y=\"-33.572850823402405\" z=\"-51.5217399597168\"/>\n" +
				"\t\t\t\t<vector3 x=\"9.260092735290527\" y=\"-33.5715309381485\" z=\"-46.57013702392578\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>e29b84cd-d3d4-422d-be32-a014051bdd07</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"16.283199310302734\" y=\"-33.572850823402405\" z=\"-51.5217399597168\"/>\n" +
				"\t\t\t\t<vector3 x=\"9.260092735290527\" y=\"-33.5715309381485\" z=\"-46.57013702392578\"/>\n" +
				"\t\t\t\t<vector3 x=\"0.0011192169040441513\" y=\"-33.26103365421295\" z=\"-37.70669174194336\"/>\n" +
				"\t\t\t\t<vector3 x=\"0.11879115551710129\" y=\"-33.19326937198639\" z=\"-27.06652069091797\"/>\n" +
				"\t\t\t\t<vector3 x=\"5.719111442565918\" y=\"-33.18970263004303\" z=\"-18.683988571166992\"/>\n" +
				"\t\t\t\t<vector3 x=\"15.704840660095215\" y=\"-33.15605318546295\" z=\"-9.33475112915039\"/>\n" +
				"\t\t\t\t<vector3 x=\"20.799619674682617\" y=\"-33.25921022891998\" z=\"-13.25214672088623\"/>\n" +
				"\t\t\t\t<vector3 x=\"24.775779724121094\" y=\"-33.57833635807037\" z=\"-40.28288269042969\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>3508a3ca-0b27-4583-93ce-a014051be310</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"30.36729621887207\" y=\"-33.57468569278717\" z=\"-44.22378921508789\"/>\n" +
				"\t\t\t\t<vector3 x=\"24.775779724121094\" y=\"-33.57833635807037\" z=\"-40.28288269042969\"/>\n" +
				"\t\t\t\t<vector3 x=\"20.799619674682617\" y=\"-33.25921022891998\" z=\"-13.25214672088623\"/>\n" +
				"\t\t\t\t<vector3 x=\"31.677350997924805\" y=\"-33.43983995914459\" z=\"9.23015308380127\"/>\n" +
				"\t\t\t\t<vector3 x=\"44.24224853515625\" y=\"-33.54519426822662\" z=\"30.96666145324707\"/>\n" +
				"\t\t\t\t<vector3 x=\"56.586273193359375\" y=\"-33.55170214176178\" z=\"23.679323196411133\"/>\n" +
				"\t\t\t\t<vector3 x=\"51.990596771240234\" y=\"-33.620851159095764\" z=\"-6.266686916351318\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>fa6b6893-7892-465e-b091-a01e1540bc50</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"15.704840660095215\" y=\"-33.15605318546295\" z=\"-9.33475112915039\"/>\n" +
				"\t\t\t\t<vector3 x=\"11.689560890197754\" y=\"-36.77850878238678\" z=\"-5.409599781036377\"/>\n" +
				"\t\t\t\t<vector3 x=\"1.4074090719223022\" y=\"-36.741151452064514\" z=\"-15.060563087463379\"/>\n" +
				"\t\t\t\t<vector3 x=\"5.719111442565918\" y=\"-33.18970263004303\" z=\"-18.683988571166992\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>1e3cd953-2876-4374-a95f-a01e1540c90a</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"31.380233764648438\" y=\"-33.43187868595123\" z=\"9.324065208435059\"/>\n" +
				"\t\t\t\t<vector3 x=\"25.07047462463379\" y=\"-33.35341799259186\" z=\"12.229236602783203\"/>\n" +
				"\t\t\t\t<vector3 x=\"3.3331334590911865\" y=\"-33.37131655216217\" z=\"27.901748657226562\"/>\n" +
				"\t\t\t\t<vector3 x=\"-1.4326344728469849\" y=\"-33.43950808048248\" z=\"32.993961334228516\"/>\n" +
				"\t\t\t\t<vector3 x=\"14.755936622619629\" y=\"-33.55372393131256\" z=\"52.28002166748047\"/>\n" +
				"\t\t\t\t<vector3 x=\"44.101905822753906\" y=\"-33.543519616127014\" z=\"31.00190544128418\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>bfe888e2-ba3f-4ef6-8f82-a01e15430b3e</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"31.380233764648438\" y=\"-33.43187868595123\" z=\"9.324065208435059\"/>\n" +
				"\t\t\t\t<vector3 x=\"25.878662109375\" y=\"-26.569417595863342\" z=\"-1.4553687572479248\"/>\n" +
				"\t\t\t\t<vector3 x=\"21.318920135498047\" y=\"-26.571143746376038\" z=\"1.10862135887146\"/>\n" +
				"\t\t\t\t<vector3 x=\"25.07047462463379\" y=\"-33.35341799259186\" z=\"12.229236602783203\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>f6e2600a-9ba6-4058-923d-a01e1540d322</id>\n" +
				"\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"25.878662109375\" y=\"-26.569417595863342\" z=\"-1.4553687572479248\"/>\n" +
				"\t\t\t\t<vector3 x=\"21.318920135498047\" y=\"-26.571143746376038\" z=\"1.10862135887146\"/>\n" +
				"\t\t\t\t<vector3 x=\"15.885750770568848\" y=\"-26.57643473148346\" z=\"-9.301880836486816\"/>\n" +
				"\t\t\t\t<vector3 x=\"20.388778686523438\" y=\"-26.560704827308655\" z=\"-12.974425315856934\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>a606ae30-28bb-499c-813a-a01e1540d8db</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"20.388778686523438\" y=\"-26.560704827308655\" z=\"-12.974425315856934\"/>\n" +
				"\t\t\t\t<vector3 x=\"36.021583557128906\" y=\"-19.013255715370178\" z=\"-19.504507064819336\"/>\n" +
				"\t\t\t\t<vector3 x=\"42.14291763305664\" y=\"-19.02014696598053\" z=\"-7.879150867462158\"/>\n" +
				"\t\t\t\t<vector3 x=\"25.878662109375\" y=\"-26.569417595863342\" z=\"-1.4553687572479248\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>0083805a-67cd-4e55-992b-a01e1540dd61</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"20.388778686523438\" y=\"-26.560704827308655\" z=\"-12.974425315856934\"/>\n" +
				"\t\t\t\t<vector3 x=\"15.885750770568848\" y=\"-26.57643473148346\" z=\"-9.301880836486816\"/>\n" +
				"\t\t\t\t<vector3 x=\"12.42266845703125\" y=\"-21.88476526737213\" z=\"-14.513218879699707\"/>\n" +
				"\t\t\t\t<vector3 x=\"16.602405548095703\" y=\"-21.91667330265045\" z=\"-17.934497833251953\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>160f2fbc-39f1-4e7d-8061-a01e1540e345</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"16.602405548095703\" y=\"-21.91667330265045\" z=\"-17.934497833251953\"/>\n" +
				"\t\t\t\t<vector3 x=\"12.720307350158691\" y=\"-21.916867852211\" z=\"-23.337251663208008\"/>\n" +
				"\t\t\t\t<vector3 x=\"7.0073628425598145\" y=\"-21.73161280155182\" z=\"-19.22061538696289\"/>\n" +
				"\t\t\t\t<vector3 x=\"12.42266845703125\" y=\"-21.88476526737213\" z=\"-14.513218879699707\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>0f2869f3-0aa9-43ee-953f-a01e1540e9b7</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"7.0073628425598145\" y=\"-21.73161280155182\" z=\"-19.22061538696289\"/>\n" +
				"\t\t\t\t<vector3 x=\"12.42266845703125\" y=\"-21.88476526737213\" z=\"-14.513218879699707\"/>\n" +
				"\t\t\t\t<vector3 x=\"4.529225826263428\" y=\"-16.403029084205627\" z=\"-9.00352954864502\"/>\n" +
				"\t\t\t\t<vector3 x=\"0.42904382944107056\" y=\"-16.399545311927795\" z=\"-14.46071720123291\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>3e55ac3a-59da-48cb-a1f1-a01e1540f048</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"0.42904382944107056\" y=\"-16.399545311927795\" z=\"-14.46071720123291\"/>\n" +
				"\t\t\t\t<vector3 x=\"4.529225826263428\" y=\"-16.403029084205627\" z=\"-9.00352954864502\"/>\n" +
				"\t\t\t\t<vector3 x=\"10.636507987976074\" y=\"-16.41952669620514\" z=\"-0.3869361877441406\"/>\n" +
				"\t\t\t\t<vector3 x=\"21.173646926879883\" y=\"-16.416901230812073\" z=\"14.38525390625\"/>\n" +
				"\t\t\t\t<vector3 x=\"19.913455963134766\" y=\"-16.417651772499084\" z=\"20.87004852294922\"/>\n" +
				"\t\t\t\t<vector3 x=\"13.39967155456543\" y=\"-16.419899582862854\" z=\"25.532747268676758\"/>\n" +
				"\t\t\t\t<vector3 x=\"6.872631072998047\" y=\"-16.439725518226624\" z=\"24.65283966064453\"/>\n" +
				"\t\t\t\t<vector3 x=\"-3.788703680038452\" y=\"-16.410095810890198\" z=\"9.981696128845215\"/>\n" +
				"\t\t\t\t<vector3 x=\"-9.886792182922363\" y=\"-16.40362322330475\" z=\"1.3677986860275269\"/>\n" +
				"\t\t\t\t<vector3 x=\"-13.76087760925293\" y=\"-16.401325821876526\" z=\"-4.2696757316589355\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>7019c157-7806-4196-8bac-a01e1540f74e</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"19.913455963134766\" y=\"-16.417651772499084\" z=\"20.87004852294922\"/>\n" +
				"\t\t\t\t<vector3 x=\"13.39967155456543\" y=\"-16.419899582862854\" z=\"25.532747268676758\"/>\n" +
				"\t\t\t\t<vector3 x=\"18.282529830932617\" y=\"-13.701066613197327\" z=\"32.37987518310547\"/>\n" +
				"\t\t\t\t<vector3 x=\"24.818891525268555\" y=\"-13.699772477149963\" z=\"27.70053482055664\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>84250556-90cb-4d27-9138-a01e1540fcc8</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"24.818891525268555\" y=\"-13.699772477149963\" z=\"27.70053482055664\"/>\n" +
				"\t\t\t\t<vector3 x=\"30.80025863647461\" y=\"-13.699802994728088\" z=\"23.913036346435547\"/>\n" +
				"\t\t\t\t<vector3 x=\"35.43284606933594\" y=\"-13.70010530948639\" z=\"19.985525131225586\"/>\n" +
				"\t\t\t\t<vector3 x=\"42.27336502075195\" y=\"-13.689524292945862\" z=\"29.4941463470459\"/>\n" +
				"\t\t\t\t<vector3 x=\"14.461579322814941\" y=\"-13.691929459571838\" z=\"49.478187561035156\"/>\n" +
				"\t\t\t\t<vector3 x=\"7.791877269744873\" y=\"-13.701715111732483\" z=\"40.19816589355469\"/>\n" +
				"\t\t\t\t<vector3 x=\"12.58993911743164\" y=\"-13.699805855751038\" z=\"36.68239212036133\"/>\n" +
				"\t\t\t\t<vector3 x=\"18.282529830932617\" y=\"-13.701066613197327\" z=\"32.37987518310547\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>41a08deb-e8f1-4098-b5ac-a01e154104be</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"25.198871612548828\" y=\"-33.34205400943756\" z=\"12.102229118347168\"/>\n" +
				"\t\t\t\t<vector3 x=\"22.65346336364746\" y=\"-33.381288170814514\" z=\"8.459278106689453\"/>\n" +
				"\t\t\t\t<vector3 x=\"0.9711512923240662\" y=\"-33.38185656070709\" z=\"24.357332229614258\"/>\n" +
				"\t\t\t\t<vector3 x=\"3.1625711917877197\" y=\"-33.35303270816803\" z=\"28.18186378479004\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>639785cd-6689-4a9e-b497-a01e15410a59</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"22.65346336364746\" y=\"-33.381288170814514\" z=\"8.459278106689453\"/>\n" +
				"\t\t\t\t<vector3 x=\"0.9711512923240662\" y=\"-33.38185656070709\" z=\"24.357332229614258\"/>\n" +
				"\t\t\t\t<vector3 x=\"-4.085421085357666\" y=\"-36.784711480140686\" z=\"19.18869972229004\"/>\n" +
				"\t\t\t\t<vector3 x=\"18.674163818359375\" y=\"-36.78494036197662\" z=\"2.6535067558288574\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>f7edd911-55db-4c2a-bd0b-a01e15410f22</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"18.674163818359375\" y=\"-36.78494036197662\" z=\"2.6535067558288574\"/>\n" +
				"\t\t\t\t<vector3 x=\"-4.085421085357666\" y=\"-36.784711480140686\" z=\"19.18869972229004\"/>\n" +
				"\t\t\t\t<vector3 x=\"-8.94637393951416\" y=\"-36.78091204166412\" z=\"9.170804977416992\"/>\n" +
				"\t\t\t\t<vector3 x=\"-14.659736633300781\" y=\"-36.772992730140686\" z=\"-3.5167768001556396\"/>\n" +
				"\t\t\t\t<vector3 x=\"1.4225962162017822\" y=\"-36.752782464027405\" z=\"-15.071061134338379\"/>\n" +
				"\t\t\t\t<vector3 x=\"11.689560890197754\" y=\"-36.77850878238678\" z=\"-5.409599781036377\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>e85b857b-6834-471b-a136-a01e154113a0</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"41.96440124511719\" y=\"-19.037875771522522\" z=\"-7.724781036376953\"/>\n" +
				"\t\t\t\t<vector3 x=\"47.131752014160156\" y=\"-19.036048531532288\" z=\"21.034948348999023\"/>\n" +
				"\t\t\t\t<vector3 x=\"64.38333129882812\" y=\"-19.03976023197174\" z=\"20.3245849609375\"/>\n" +
				"\t\t\t\t<vector3 x=\"62.956581115722656\" y=\"-18.93619692325592\" z=\"2.6148407459259033\"/>\n" +
				"\t\t\t\t<vector3 x=\"57.27806854248047\" y=\"-18.901294350624084\" z=\"-15.444388389587402\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>d852e478-feca-44b8-8684-a01e15411845</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"47.98801803588867\" y=\"-18.81707727909088\" z=\"-31.895301818847656\"/>\n" +
				"\t\t\t\t<vector3 x=\"34.8671760559082\" y=\"-18.858808159828186\" z=\"-46.580326080322266\"/>\n" +
				"\t\t\t\t<vector3 x=\"18.5678653717041\" y=\"-18.997260689735413\" z=\"-57.49827194213867\"/>\n" +
				"\t\t\t\t<vector3 x=\"25.65540885925293\" y=\"-19.038713097572327\" z=\"-33.15637969970703\"/>\n" +
				"\t\t\t\t<vector3 x=\"35.84016418457031\" y=\"-19.01314127445221\" z=\"-19.880626678466797\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>34dca109-a745-4d75-a182-a01e15411d43</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"18.5678653717041\" y=\"-18.997260689735413\" z=\"-57.49827194213867\"/>\n" +
				"\t\t\t\t<vector3 x=\"25.65540885925293\" y=\"-19.038713097572327\" z=\"-33.15637969970703\"/>\n" +
				"\t\t\t\t<vector3 x=\"6.814425468444824\" y=\"-19.061757683753967\" z=\"-44.920467376708984\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>8980241e-6994-48d1-99d9-a02018560c68</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"41.96440124511719\" y=\"-19.037875771522522\" z=\"-7.724781036376953\"/>\n" +
				"\t\t\t\t<vector3 x=\"57.27806854248047\" y=\"-18.901294350624084\" z=\"-15.444388389587402\"/>\n" +
				"\t\t\t\t<vector3 x=\"47.98801803588867\" y=\"-18.81707727909088\" z=\"-31.895301818847656\"/>\n" +
				"\t\t\t\t<vector3 x=\"35.84016418457031\" y=\"-19.01314127445221\" z=\"-19.880626678466797\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>82c44b61-e6a5-4817-a3e7-a01e154121a3</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"52.501461029052734\" y=\"-37.426644921302795\" z=\"-85.62081909179688\"/>\n" +
				"\t\t\t\t<vector3 x=\"42.428043365478516\" y=\"-37.418126702308655\" z=\"-98.2367172241211\"/>\n" +
				"\t\t\t\t<vector3 x=\"27.381399154663086\" y=\"-37.402082085609436\" z=\"-124.4727554321289\"/>\n" +
				"\t\t\t\t<vector3 x=\"66.0037841796875\" y=\"-37.27414095401764\" z=\"-152.8318328857422\"/>\n" +
				"\t\t\t\t<vector3 x=\"111.9771957397461\" y=\"-37.31739580631256\" z=\"-105.8241958618164\"/>\n" +
				"\t\t\t\t<vector3 x=\"111.07829284667969\" y=\"-37.131444573402405\" z=\"-80.79582977294922\"/>\n" +
				"\t\t\t\t<vector3 x=\"77.8495864868164\" y=\"-37.09781801700592\" z=\"-56.44760513305664\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>fca7ad2d-389b-4061-b487-a01e1541604c</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"111.07829284667969\" y=\"-37.131444573402405\" z=\"-80.79582977294922\"/>\n" +
				"\t\t\t\t<vector3 x=\"77.8495864868164\" y=\"-37.09781801700592\" z=\"-56.44760513305664\"/>\n" +
				"\t\t\t\t<vector3 x=\"123.41226959228516\" y=\"-36.58955156803131\" z=\"-56.816158294677734\"/>\n" +
				"\t\t\t\t<vector3 x=\"116.28728485107422\" y=\"-36.60802614688873\" z=\"-38.9024772644043\"/>\n" +
				"\t\t\t\t<vector3 x=\"97.7969741821289\" y=\"-36.594361901283264\" z=\"-36.38414764404297\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>62b4f701-5482-4515-9e18-a01e15416511</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"0.05194097384810448\" y=\"-33.174508690834045\" z=\"-27.15190315246582\"/>\n" +
				"\t\t\t\t<vector3 x=\"5.610574245452881\" y=\"-33.21108400821686\" z=\"-18.695241928100586\"/>\n" +
				"\t\t\t\t<vector3 x=\"-0.10201774537563324\" y=\"-29.24038851261139\" z=\"-15.929877281188965\"/>\n" +
				"\t\t\t\t<vector3 x=\"-5.654193878173828\" y=\"-29.237390160560608\" z=\"-23.437335968017578\"/>\n" +
				"\t\t\t</points>\n" +
				"\t\t\t<id>0c34afb2-b594-4dbf-8b0a-a01e154169a7</id>\n" +
				"\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t\t<se.spaced.shared.world.area.Polygon>\n" +
				"\n" +
				"\t\t\t<points>\n" +
				"\t\t\t\t<vector3 x=\"-5.654193878173828\" y=\"-29.237390160560608\" z=\"-23.437335968017578\"/>\n" +
				"\t\t\t\t<vector3 x=\"-0.10201774537563324\" y=\"-29.24038851261139\" z=\"-15.929877281188965\"/>\n" +
				"\t\t\t\t<vector3 x=\"-15.329190254211426\" y=\"-29.2703777551651\" z=\"-4.956465244293213\"/>\n" +
				"\t\t\t\t<vector3 x=\"-20.345762252807617\" y=\"-29.239261269569397\" z=\"-12.844050407409668\"/>\n" +
				"\t\t\t\t<vector3 x=\"-20.718475341796875\" y=\"-29.23739206790924\" z=\"-14.585307121276855\"/>\n" +
				"\t\t\t\t<vector3 x=\"-7.172074794769287\" y=\"-29.236858010292053\" z=\"-24.231639862060547\"/>\n" +
				"\t\t\t</points>\n" +
				"\n" +
				"\t\t\t<id>3e36b4e7-550c-49fe-b3d5-a01e15417122</id>\n" +
				"\t\t</se.spaced.shared.world.area.Polygon>\n" +
				"\t</polygons>\n" +
				"\n" +
				"\t<connections>\n" +
				"\t\t<connection>\n" +
				"\t\t\t<from>860ba91f-a298-4897-847f-a014051bc8f7</from>\n" +
				"\t\t\t<to>fcb41f40-5cda-4bc1-bb9c-a014051bd21a</to>\n" +
				"\t\t\t<point1 x=\"39.769412994384766\" y=\"-37.42752993106842\" z=\"-80.98175811767578\"/>\n" +
				"\t\t\t<point2 x=\"34.35565948486328\" y=\"-37.4252792596817\" z=\"-87.86817932128906\"/>\n" +
				"\t\t\t<direction>BIDIRECTIONAL</direction>\n" +
				"\t\t</connection>\n" +
				"\t\t<connection>\n" +
				"\t\t\t<from>fcb41f40-5cda-4bc1-bb9c-a014051bd21a</from>\n" +
				"\t\t\t<to>2aa76c06-8f0e-4d98-9837-a014051bd732</to>\n" +
				"\t\t\t<point1 x=\"-1.770926594734192\" y=\"-37.4385849237442\" z=\"-61.91138458251953\"/>\n" +
				"\t\t\t<point2 x=\"10.407261848449707\" y=\"-37.45839846134186\" z=\"-59.860774993896484\"/>\n" +
				"\t\t\t<direction>BIDIRECTIONAL</direction>\n" +
				"\t\t</connection>\n" +
				"\t\t<connection>\n" +
				"\t\t\t<from>2aa76c06-8f0e-4d98-9837-a014051bd732</from>\n" +
				"\t\t\t<to>e29b84cd-d3d4-422d-be32-a014051bdd07</to>\n" +
				"\t\t\t<point1 x=\"4.322529315948486\" y=\"-37.33474123477936\" z=\"-53.440635681152344\"/>\n" +
				"\t\t\t<point2 x=\"11.363594055175781\" y=\"-37.285714745521545\" z=\"-58.383304595947266\"/>\n" +
				"\t\t\t<direction>BIDIRECTIONAL</direction>\n" +
				"\t\t</connection>\n" +
				"\t\t<connection>\n" +
				"\t\t\t<from>e29b84cd-d3d4-422d-be32-a014051bdd07</from>\n" +
				"\t\t\t<to>3508a3ca-0b27-4583-93ce-a014051be310</to>\n" +
				"\t\t\t<point1 x=\"16.283199310302734\" y=\"-33.572850823402405\" z=\"-51.5217399597168\"/>\n" +
				"\t\t\t<point2 x=\"9.260092735290527\" y=\"-33.5715309381485\" z=\"-46.57013702392578\"/>\n" +
				"\t\t\t<direction>BIDIRECTIONAL</direction>\n" +
				"\t\t</connection>\n" +
				"\t</connections>\n" +
				"</walkmesh>";

		return data.getBytes();
	}

}
