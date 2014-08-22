<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="ProxyEndpoint|TargetEndpoint">
        <xsl:copy>
            <xsl:copy-of select="@*|FaultRules|PreFlow|PostFlow|Flows"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>