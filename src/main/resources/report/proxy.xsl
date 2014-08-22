<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html lang="en">
            <head>
                <meta charset="utf-8"/>
                <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>Apigee Proxy Coverage Report</title>
                <link href="bootstrap.min.css" rel="stylesheet"/>
            </head>
            <body>
                <section id="heading">
                    <div class="well">
                        <div class="row">
                            <div class="col-lg-12" align="center">
                                <h4>
                                        Endpoint:
                                        <xsl:value-of select="ProxyEndpoint/@name"/>
                                        <xsl:value-of select="TargetEndpoint/@name"/>
                                        <br/>
                                        <small>(Policy Execution Stats)</small>
                                </h4>
                            </div>
                        </div>
                    </div>
                </section>

                <section id="Execution Details">
                    <div class="container">
                        <xsl:apply-templates select="//PreFlow"/>
                        <p/>
                        <xsl:apply-templates select="//PostFlow"/>
                        <p/>
                        <xsl:for-each select="//FaultRules/FaultRule">
                            <xsl:apply-templates select="."/>
                            <p/>
                        </xsl:for-each>
                        <xsl:for-each select="//Flows/Flow">
                            <xsl:apply-templates select="."/>
                            <p/>
                        </xsl:for-each>
                    </div>
                </section>

                <section id="footer">
                    <div class="page-header"></div>
                    <div class="container">
                        <div class="row">
                            <div class="col-lg-12">
                                <div class="col-lg-12">
                                    <ul class="list-unstyled">
                                        <li class="pull-right">
                                            <a href="#top">Back to top</a>
                                        </li>
                                    </ul>
                                    <p>Implemented by <a href="mailto:sriki77@gmail.com" rel="nofollow">Srikanth
                                        Seshadri</a>.
                                    </p>

                                    <p>Code released under the <a
                                            href="https://github.com/sriki77/apigee-proxy-coverage/blob/master/LICENSE">
                                        MIT License</a>.
                                        Based on <a href="http://getbootstrap.com" rel="nofollow">Bootstrap</a>.
                                        Using <a href="http://bootswatch.com/readable/" rel="nofollow">Bootswatch
                                            Readable Theme</a>.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </body>
        </html>
    </xsl:template>
    <xsl:template match="PreFlow|PostFlow|Flow">
        <div class="row">
            <a>
                <xsl:attribute name="name">
                    <xsl:value-of select="@name"/>
                </xsl:attribute>
            </a>
            <div class="col-lg-2"><p/></div>
            <div class="col-lg-8">
                <h4><span class="text-muted">Flow: <xsl:value-of select="@name"/></span></h4>
                <table class="table">
                    <thead>
                        <tr>
                            <th></th>
                            <th><small>Policy Name</small></th>
                            <th><small>Condition</small></th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:if test="Request/Step">
                            <tr class="text-primary">
                                <td colspan="3" align="center">
                                <small>Request Flow</small>
                                </td>
                            </tr>
                            <xsl:apply-templates select="Request"/>
                        </xsl:if>
                        <xsl:if test="Response/Step">
                            <tr class="text-primary">
                                <td colspan="3" align="center">
                                <small>Response Flow</small>
                                </td>
                            </tr>
                            <xsl:apply-templates select="Response"/>
                        </xsl:if>
                    </tbody>
                </table>
            </div>
             <div class="col-lg-2"><p/></div>
        </div>
    </xsl:template>
    <xsl:template match="FaultRule">
        <div class="row">
            <a>
                <xsl:attribute name="name">
                    <xsl:value-of select="@name"/>
                </xsl:attribute>
            </a>
             <div class="col-lg-2"><p/></div>
            <div class="col-lg-8">

                <h4><span class="text-muted">Fault Rule: <xsl:value-of select="@name"/></span></h4>
                <table class="table">
                    <thead>
                        <tr>
                            <th></th>
                            <th><small>Policy Name</small></th>
                            <th><small>Condition</small></th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:for-each select="Step">
                            <xsl:apply-templates select="."/>
                        </xsl:for-each>
                    </tbody>
                </table>
            </div>
             <div class="col-lg-2"><p/></div>
        </div>
    </xsl:template>
    <xsl:template match="Request|Response">
        <xsl:for-each select="Step">
            <xsl:apply-templates select="."/>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="Step">
        <tr>
            <td>
                 <xsl:choose>
                        <xsl:when test="@executed='true'">
                            <span class="text-success"> &#10004; </span>
                        </xsl:when>
                        <xsl:otherwise>
                             <span class="text-danger"> &#10008; </span>
                        </xsl:otherwise>
                    </xsl:choose>
            </td>
            <td>

                <small>
                    <xsl:choose>
                        <xsl:when test="@executed='true'">
                            <span class="text-success"><xsl:value-of select="Name"/></span>
                        </xsl:when>
                        <xsl:otherwise>
                             <span class="text-danger"> <xsl:value-of select="Name"/></span>
                        </xsl:otherwise>
                    </xsl:choose>
                    
                </small>
            </td>
            <td>
                <xsl:choose>
                    <xsl:when test="Condition">
                        <small>
                             <xsl:choose>
                                <xsl:when test="@executed='true'">
                                    <span class="text-success"><xsl:value-of select="Condition"/></span>
                                </xsl:when>
                                <xsl:otherwise>
                                     <span class="text-danger"> <xsl:value-of select="Condition"/></span>
                                </xsl:otherwise>
                            </xsl:choose>
                        </small>
                    </xsl:when>
                    <xsl:otherwise>
                        -
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>