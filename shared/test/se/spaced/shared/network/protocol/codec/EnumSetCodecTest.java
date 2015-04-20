package se.spaced.shared.network.protocol.codec;

import org.junit.Before;
import org.junit.Test;
import se.smrt.core.remote.DefaultCodecImpl;
import se.smrt.core.remote.DefaultReadCodec;
import se.smrt.core.remote.DefaultWriteCodec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnumSetCodecTest {
	private EnumSetCodec enumSetCodec;
	private DefaultWriteCodec writeCodec;
	private DefaultReadCodec readCodec;


	@Before
	public void setUp() throws Exception {
		enumSetCodec = new EnumSetCodec();
		writeCodec = new DefaultCodecImpl();
		readCodec = new DefaultCodecImpl();
	}

	@Test
	public void testThreeEnum() throws IOException {
		testWriteEnum(7L, ThreeEnum.class);
		testWriteAndReadEnum(ThreeEnum.values(), ThreeEnum.class);
	}

	@Test
	public void testSixtyThreeEnum() throws IOException {
		testWriteEnum(9223372036854775807L, SixtyThreeEnum.class);
		testWriteAndReadEnum(SixtyThreeEnum.values(), SixtyThreeEnum.class);
	}

	@Test
	public void testSixtyFourEnum() throws IOException {
		testWriteEnum(-1L, SixtyFourEnum.class);
		testWriteAndReadEnum(SixtyFourEnum.values(), SixtyFourEnum.class);
	}

	@Test
	public void testSixtyFiveEnum() throws IOException {
		long[] expectedValues = {-1, 1};
		testWriteEnum(expectedValues, SixtyFiveEnum.class);
		testWriteAndReadEnum(SixtyFiveEnum.values(), SixtyFiveEnum.class);
	}

	@Test
	public void testLargeEnum() throws IOException {
		testWriteAndReadEnum(LargeEnum.values(), LargeEnum.class);
	}

	@Test
	public void testJustSomeEnumValues() throws IOException {
		EnumSet<LargeEnum> enumSet = EnumSet.allOf(LargeEnum.class);

		enumSet.remove(LargeEnum.V1);
		enumSet.remove(LargeEnum.V10);
		enumSet.remove(LargeEnum.V15);
		enumSet.remove(LargeEnum.V69);
		enumSet.remove(LargeEnum.V200);
		enumSet.remove(LargeEnum.V258);

		byte[] bytes = writeEnumSet(enumSet);
		InputStream is = new ByteArrayInputStream(bytes);

		EnumSet<LargeEnum> copiedEnumSet = enumSetCodec.readEnumSet(readCodec, is, LargeEnum.class);

		assertEquals(enumSet.size(), copiedEnumSet.size());
		for (LargeEnum value : enumSet) {
			assertTrue(copiedEnumSet.contains(value));
		}
	}

	private <E extends Enum<E>> void testWriteEnum(long[] expectedValues, Class<E> enumClass) throws IOException {
		EnumSet<E> allOf = EnumSet.allOf(enumClass);
		byte[] bytes = writeEnumSet(allOf);
		long[] values = readLongs(bytes);
		assertArrayEquals(expectedValues, values);
	}


	private <E extends Enum<E>> void testWriteEnum(long expectedValue, Class<E> enumClass) throws IOException {
		EnumSet<E> allOf = EnumSet.allOf(enumClass);
		byte[] bytes = writeEnumSet(allOf);
		assertEquals(expectedValue, readLong(bytes));
	}

	private <E extends Enum<E>> void testWriteAndReadEnum(Enum<E>[] values, Class<E> enumClass) throws IOException {
		EnumSet<E> allOf = EnumSet.allOf(enumClass);
		byte[] bytes = writeEnumSet(allOf);
		InputStream is = new ByteArrayInputStream(bytes);

		EnumSet<E> enumSet = enumSetCodec.readEnumSet(readCodec, is, enumClass);
		assertEquals(values.length, enumSet.size());
		for (Enum<E> value : values) {
			assertTrue(enumSet.contains(value));
		}
	}


	private byte[] writeEnumSet(EnumSet<?> enumSet) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enumSetCodec.writeEnumSet(writeCodec, baos, enumSet);
		return baos.toByteArray();

	}


	private long readLong(byte[] bytes) throws IOException {
		return readCodec.readLong(new ByteArrayInputStream(bytes));
	}

	private long[] readLongs(byte[] bytes) throws IOException {

		InputStream bais = new ByteArrayInputStream(bytes);
		int length = readCodec.readInt(bais);
		long[] values = new long[length];
		for (int i = 0; i < length; i++) {
			values[i] = readCodec.readLong(bais);
		}
		return values;
	}

	public enum ThreeEnum {
		ONE, TWO, THREE
	}

	public enum SixtyThreeEnum {
		V1, V2, V3, V4, V5, V6, V7, V8, V9, V10,
		V11, V12, V13, V14, V15, V16, V17, V18, V19, V20,
		V21, V22, V23, V24, V25, V26, V27, V28, V29, V30,
		V31, V32, V33, V34, V35, V36, V37, V38, V39, V40,
		V41, V42, V43, V44, V45, V46, V47, V48, V49, V50,
		V51, V52, V53, V54, V55, V56, V57, V58, V59, V60,
		V61, V62, V63
	}

	public enum SixtyFourEnum {
		V1, V2, V3, V4, V5, V6, V7, V8, V9, V10,
		V11, V12, V13, V14, V15, V16, V17, V18, V19, V20,
		V21, V22, V23, V24, V25, V26, V27, V28, V29, V30,
		V31, V32, V33, V34, V35, V36, V37, V38, V39, V40,
		V41, V42, V43, V44, V45, V46, V47, V48, V49, V50,
		V51, V52, V53, V54, V55, V56, V57, V58, V59, V60,
		V61, V62, V63, V64
	}

	public enum SixtyFiveEnum {
		V1, V2, V3, V4, V5, V6, V7, V8, V9, V10,
		V11, V12, V13, V14, V15, V16, V17, V18, V19, V20,
		V21, V22, V23, V24, V25, V26, V27, V28, V29, V30,
		V31, V32, V33, V34, V35, V36, V37, V38, V39, V40,
		V41, V42, V43, V44, V45, V46, V47, V48, V49, V50,
		V51, V52, V53, V54, V55, V56, V57, V58, V59, V60,
		V61, V62, V63, V64, V65
	}

	public enum LargeEnum {
		V1, V2, V3, V4, V5, V6, V7, V8, V9,
		V10, V11, V12, V13, V14, V15, V16, V17, V18, V19,
		V20, V21, V22, V23, V24, V25, V26, V27, V28, V29,
		V30, V31, V32, V33, V34, V35, V36, V37, V38, V39,
		V40, V41, V42, V43, V44, V45, V46, V47, V48, V49,
		V50, V51, V52, V53, V54, V55, V56, V57, V58, V59,
		V60, V61, V62, V63, V64, V65, V66, V67, V68, V69,
		V70, V71, V72, V73, V74, V75, V76, V77, V78, V79,
		V80, V81, V82, V83, V84, V85, V86, V87, V88, V89,
		V90, V91, V92, V93, V94, V95, V96, V97, V98, V99,
		V100, V101, V102, V103, V104, V105, V106, V107, V108, V109,
		V110, V111, V112, V113, V114, V115, V116, V117, V118, V119,
		V120, V121, V122, V123, V124, V125, V126, V127, V128, V129,
		V130, V131, V132, V133, V134, V135, V136, V137, V138, V139,
		V140, V141, V142, V143, V144, V145, V146, V147, V148, V149,
		V150, V151, V152, V153, V154, V155, V156, V157, V158, V159,
		V160, V161, V162, V163, V164, V165, V166, V167, V168, V169,
		V170, V171, V172, V173, V174, V175, V176, V177, V178, V179,
		V180, V181, V182, V183, V184, V185, V186, V187, V188, V189,
		V190, V191, V192, V193, V194, V195, V196, V197, V198, V199,
		V200, V201, V202, V203, V204, V205, V206, V207, V208, V209,
		V210, V211, V212, V213, V214, V215, V216, V217, V218, V219,
		V220, V221, V222, V223, V224, V225, V226, V227, V228, V229,
		V230, V231, V232, V233, V234, V235, V236, V237, V238, V239,
		V240, V241, V242, V243, V244, V245, V246, V247, V248, V249,
		V250, V251, V252, V253, V254, V255, V256, V257, V258
	}


}
