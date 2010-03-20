<?xml version="1.0" encoding="windows-1251"?>
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:l="http://www.w3.org/1999/xlink"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:fb="http://www.gribuser.ru/xml/fictionbook/2.0">
	
	
						xmlns:dc="http://purl.org/metadata/dublin_core"
						xmlns:oebpackage="http://openebook.org/namespaces/oeb-package/1.0/">
					<title>
					<xsl:value-of disable-output-escaping="yes" select="description/title-info/book-title"/>
					</title>
				-->
					select="description/title-info/coverpage/image" />
						select="description/title-info/book-title" />
				</h1>
							select="description/title-info/author">
							select="description/title-info/sequence">
						select="description/title-info/annotation">
					test="$tocdepth &gt; 0 and count(//body[not(@name) or @name != 'notes']//title) &gt; 1">
								mode="toc" />
			select="middle-name" />
				select="@number" />
			</xsl:when>
						filepos="000000000">
						<xsl:value-of disable-output-escaping="yes"
							select="$NotesTitle" />
					</a>
				</li>
			</xsl:otherwise>
			test="title | .//section[count(ancestor::section) &lt; $tocdepth]/title">
					test="(.//section/title) and (count(ancestor::section) &lt; $tocdepth or $tocdepth=	4)">
							mode="toc" />
					</UL>
						select="normalize-space(p[1])" />
						</xsl:if>
							select="normalize-space(.)" />
		</div>
				test="ancestor::body/@name = 'notes' and not(following-sibling::section)">
					<xsl:for-each select="parent::section">
					<xsl:call-template name="preexisting_id"/>
					</xsl:for-each>-->
				</strong>
								name="{concat('h',count(ancestor::node())-3)}">
									name="preexisting_id" />
									name="preexisting_id" />
			</h1>
		</div>
		</b>
		</i>
		</span>
			</xsl:attribute>
				<xsl:attribute name="title">
				<xsl:choose>
				<xsl:when test="starts-with(@href,'#')"><xsl:value-of select="key('note-link',substring-after(@href,'#'))/p"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="key('note-link',@href)/p"/></xsl:otherwise>
				</xsl:choose>
				</xsl:attribute>
			-->
		</xsl:if>
				</i>
			</b>
			</i>
		</blockquote>
					select="@value" />
				</xsl:attribute>
				</xsl:attribute>
					</xsl:attribute>
					</xsl:attribute>
		</xsl:if>
							select="$NotesTitle" />
					</h4>
				</xsl:when>
							select="@name" />
					</h4>
				</xsl:when>