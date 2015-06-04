package se.ardortech.meshgenerator.text;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector2;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.util.geom.BufferUtils;
import se.ardortech.text.BMFont;

import java.nio.FloatBuffer;

public class TextMeshDataGenerator {
	/**
	 * Justification within a text block
	 */
	public enum Justify {
		Left, Center, Right;
	}

	/**
	 * Alignment of the text block from the pivot point
	 */
	public enum Align {
		North(-0.5f, 0.0f), NorthWest(0.0f, 0.0f), NorthEast(-1.0f, 0.0f), Center(-0.5f, -0.5f), West(0.0f, -0.5f), East(-1.0f, -0.5f), South(-0.5f, -1.0f), SouthWest(0.0f, -1.0f), SouthEast(-1.0f, -1.0f);
		public final float horizontal;
		public final float vertical;

		private Align(final float h, final float v) {
			horizontal = h;
			vertical = v;
		}
	}

	private final BMFont font;

	private String _textString;
	private final int _tabSize = 4;
	private final boolean useColors = true;

	private final Vector2 _size = new Vector2(); // width and height of text string

	private int _lines = 1;
	private float[] _lineWidths = new float[64]; // size of each line of text

	private final Justify justify;
	private int spacing = 0; // additional spacing between characters

	private final Align align;
	private final Vector2 alignOffset = new Vector2();
	private final Vector2 fixedOffset = new Vector2();

	public TextMeshDataGenerator(final String text, final BMFont font) {
		this(text, font, Align.SouthWest);
	}

	public TextMeshDataGenerator(final String text, final BMFont font, final Align align) {
		this(text, font, align, Justify.Left);
	}

	public TextMeshDataGenerator(final String text, final BMFont font, final Align align, final Justify justify) {
		this.font = font;
		this.align = align;
		this.justify = justify;
		spacing = 0;
		if (font.getOutlineWidth() > 1) {
			spacing = font.getOutlineWidth() - 1;
		}
		setText(text);
	}

	private void addToLineSizes(final float sizeX, final int lineIndex) {
		if (lineIndex >= _lineWidths.length) { // make sure array is big enough
			final float[] newLineSizes = new float[_lineWidths.length * 2];
			System.arraycopy(_lineWidths, 0, newLineSizes, 0, _lineWidths.length);
			_lineWidths = newLineSizes;
		}
		_lineWidths[lineIndex] = sizeX;
	}

	private void calculateSize(final String text) {
		_size.set(0, 0);

		final float lineHeight = font.getLineHeight();
		_lines = 0;

		_lineWidths[0] = 0;
		final int strLen = text.length();
		float cursorX = 0;
		float cursorY = 0;
		for (int i = 0; i < strLen; i++) {
			final int charVal = text.charAt(i);
			if (charVal == '\n') { // newline special case

				addToLineSizes(cursorX, _lines);
				_lines++;
				if (cursorX > _size.getX()) {
					_size.setX(cursorX);
				}
				cursorX = 0;
				cursorY = _lines * lineHeight;
			} else if (charVal == '\t') { // tab special case
				final float tabStop = _tabSize * font.getMaxCharAdvance();
				final float stops = 1 + (float) Math.floor(cursorX / tabStop);
				cursorX = stops * tabStop;
			} else { // normal character
				BMFont.Char chr = font.getChar(charVal);
				int nextVal = 0;
				if (i < strLen - 1) {
					nextVal = text.charAt(i + 1);
				}
				final int kern = font.getKerning(charVal, nextVal);
				cursorX += chr.xadvance + kern + spacing;
			}
		}
		addToLineSizes(cursorX, _lines);
		if (cursorX > _size.getX()) {
			_size.setX(cursorX);
		}

		_size.setY(cursorY + lineHeight);
		_lines++;
	}

	private void calculateAlignmentOffset() {
		alignOffset.set(0, 0);
		if (align != null) {
			alignOffset.setX(_size.getX() * align.horizontal);
			alignOffset.setY(_size.getY() * align.vertical);
		}
	}

	private void checkBuffers(MeshData meshData, final String text) {
		final int chunkSize = 20;
		final int numVertices = 4 * text.length();
		final int numChunks = 1 + (numVertices / chunkSize);
		final int required = numChunks * chunkSize;
		FloatBuffer vertices = meshData.getVertexBuffer();
		FloatBuffer colors = meshData.getColorBuffer();
		FloatBuffer textureCoords = meshData.getTextureBuffer(0);

		if (vertices == null || vertices.capacity() / 3 < required) {
			vertices = BufferUtils.createVector3Buffer(required);
			textureCoords = BufferUtils.createVector2Buffer(required);
			meshData.setVertexBuffer(vertices);
			meshData.setTextureBuffer(textureCoords, 0);
			if (useColors) {
				colors = BufferUtils.createVector4Buffer(required);
				meshData.setColorBuffer(colors);
			}
		}
		vertices.limit(numVertices * 3).rewind();
		textureCoords.limit(numVertices * 2).rewind();
		if (useColors) {
			colors.limit(numVertices * 4).rewind();
		}
	}

	public void setData(MeshData meshData) {
		setMeshData(meshData, _textString);
	}

	public void setCharColor(MeshData meshData, int charIndex, ColorRGBA color) {
		FloatBuffer colorBuffer = meshData.getColorBuffer();
		if (colorBuffer != null) {
			colorBuffer.position(charIndex * 16);
			for (int i = 0; i < 4; i++) {
				colorBuffer.put(color.getRed()).put(color.getGreen()).put(color.getBlue()).put(color.getAlpha());
			}
		}
	}

	public final void setText(final String text) {
		if (text == null) {
			_textString = "";
		} else {
			_textString = text;
		}
	}

	private void setMeshData(MeshData meshData, final String text) {
		checkBuffers(meshData, text);
		calculateSize(text);
		calculateAlignmentOffset();

		final FloatBuffer vertices = meshData.getVertexBuffer();
		final FloatBuffer textureCoords = meshData.getTextureBuffer(0);
		final FloatBuffer colors = meshData.getColorBuffer();

		final float txW = font.getTextureWidth();
		final float txH = font.getTextureHeight();

		int lineIndex = 0;
		float cursorX = getJustificationXOffset(lineIndex);
		final float lineHeight = font.getLineHeight();

		float alignX = _size.getXf() * align.horizontal;
		float alignY = _size.getYf() * align.vertical;
		alignX += fixedOffset.getX();
		alignY += fixedOffset.getY();

		final int strLen = text.length();
		float cursorY = 0;
		for (int i = 0; i < strLen; i++) {
			final int charVal = text.charAt(i);

			if (charVal == '\n') { // newline special case
				lineIndex++;
				cursorX = getJustificationXOffset(lineIndex);
				cursorY += lineHeight;
				addEmptyCharacter(vertices, textureCoords, colors);
			} else if (charVal == '\t') { // tab special case
				final float tabStop = _tabSize * font.getMaxCharAdvance();
				final float stops = 1 + (float) Math.floor(cursorX / tabStop);
				cursorX = stops * tabStop;
				addEmptyCharacter(vertices, textureCoords, colors);
			} else { // normal character
				BMFont.Char chr = font.getChar(charVal);

				// -- vertices -----------------
				float l = alignX + cursorX + chr.xoffset;
				float t = alignY + cursorY + chr.yoffset;
				float r = alignX + cursorX + chr.xoffset + chr.width;
				float b = alignY + cursorY + chr.yoffset + chr.height;

				vertices.put(l).put(-t).put(0); // left top
				vertices.put(l).put(-b).put(0); // left bottom
				vertices.put(r).put(-b).put(0); // right bottom
				vertices.put(r).put(-t).put(0); // right top

				// -- tex coords ----------------
				l = chr.x / txW;
				t = chr.y / txH;
				r = (chr.x + chr.width) / txW;
				b = (chr.y + chr.height) / txH;

				textureCoords.put(l).put(t); // left top
				textureCoords.put(l).put(b); // left bottom
				textureCoords.put(r).put(b); // right bottom
				textureCoords.put(r).put(t); // right top

				if (useColors) {
					for (int c = 0; c < 16; c++) {
						colors.put(1);
					}
				}

				int nextVal = 0;
				if (i < strLen - 1) {
					nextVal = text.charAt(i + 1);
				}
				final int kern = font.getKerning(charVal, nextVal);
				cursorX += chr.xadvance + kern + spacing;
			}
		}
		meshData.setColorBuffer(colors);
		meshData.setVertexBuffer(vertices);
		meshData.setTextureBuffer(textureCoords, 0);
	}

	private float getJustificationXOffset(final int lineIndex) {
		float cursorX = 0;
		switch (justify) {
		case Left:
			cursorX = 0;
			break;
		case Center:
			cursorX = 0.5f * (_size.getXf() - _lineWidths[lineIndex]);
			break;
		case Right:
			cursorX = _size.getXf() - _lineWidths[lineIndex];
			break;
		}
		return cursorX;
	}

	private void addEmptyCharacter(final FloatBuffer vertices, final FloatBuffer uvs, final FloatBuffer colors) {
		vertices.put(0).put(0).put(0);
		vertices.put(0).put(0).put(0);
		vertices.put(0).put(0).put(0);
		vertices.put(0).put(0).put(0);
		uvs.put(0).put(0);
		uvs.put(0).put(0);
		uvs.put(0).put(0);
		uvs.put(0).put(0);

		if (useColors) {
			for (int c = 0; c < 16; c++) {
				colors.put(1);
			}
		}
	}
}