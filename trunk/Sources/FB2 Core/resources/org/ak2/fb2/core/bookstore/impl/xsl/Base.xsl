<?xml version="1.0" encoding="windows-1251"?><xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:l="http://www.w3.org/1999/xlink"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:fb="http://www.gribuser.ru/xml/fictionbook/2.0">

	<xsl:output method="xml" encoding="UTF-8" />	<xsl:param name="archiveName" select="0" />
	<xsl:param name="fileName" select="0" />
	<xsl:param name="digest" select="0" />

	<xsl:template match="/*">		<xml-fragment>
			<xsl:choose>
				<xsl:when test="$archiveName = '0'">
					<xsl:element name="File">
						<xsl:value-of select="$fileName" />
					</xsl:element>
				</xsl:when>
				<xsl:otherwise>
					<xsl:element name="ArchivedFile">
						<xsl:attribute name="archive"><xsl:value-of select="$archiveName"/></xsl:attribute>
						<xsl:value-of select="$fileName" />
					</xsl:element>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:element name="Digest">
				<xsl:value-of select="$digest" />
			</xsl:element>
			<xsl:apply-templates select="description" />
		</xml-fragment>
	</xsl:template>	<xsl:template match="*">
		<xsl:element name="{name()}">
			<xsl:copy-of select="@*" />
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>