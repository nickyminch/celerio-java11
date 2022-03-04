/*
 * Copyright 2015 JAXIO http://www.jaxio.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jaxio.celerio.maven.plugin.celerio;

import com.jaxio.celerio.Config;
import com.jaxio.celerio.configuration.*;
import com.jaxio.celerio.configuration.database.JdbcConnectivity;
import com.jaxio.celerio.configuration.database.Metadata;
import com.jaxio.celerio.configuration.database.TableType;
import com.jaxio.celerio.configuration.database.support.MetadataExtractor;
import com.jaxio.celerio.configuration.support.CelerioLoader;
import com.jaxio.celerio.configuration.support.MetadataLoader;
import com.jaxio.celerio.main.CelerioProducer;
import com.jaxio.celerio.output.OutputResult;
import com.jaxio.celerio.output.OutputResultFactory;
import com.jaxio.celerio.template.pack.PackLoader;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.XmlMappingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.normalize;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * The core Celerio Engine is invoked by this plugin. This plugin can either connect directly to a database and extract the metadata information or use the
 * metadata.xml file produced by the dbmetadata-maven-plugin:extract-metadata goal. Please refer to dbmetadata.extract-metadata.
 *
 * @since 1.0.0
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = false)
public class GenerateMojo extends AbstractMojo {
    private static String DEFAULT_XML_METADATA = normalize("src/main/config/celerio-maven-plugin/metadata.xml");
    private ClassPathXmlApplicationContext context;

    /**
     * Maven project, this is by default the current maven project.
     */
    @Parameter(property = "project", required = true)
    protected MavenProject project;

    /**
     * The current folder
     */
    @Parameter(property = "basedir")
    protected String baseDir;

    /**
     * The output folder.
     */
    @Parameter(property = "celerio-maven-plugin.outputDir", defaultValue = "${basedir}")
    protected String outputDirectory;

    /**
     * The relative path to the Maven Celerio configuration file.
     * <p>
     * The default value is <div class="filename">src/main/config/celerio-maven-plugin/celerio-maven-plugin.xml</div>
     */
    @Parameter(property = "celerio-maven-plugin.configuration", defaultValue = "${basedir}/src/main/config/celerio-maven-plugin/celerio-maven-plugin.xml")
    protected String xmlConfiguration;

    /**
     * The relative path to a Maven Celerio configuration file dedicated to override the template packs definition present in the main xml configuration file.
     * This configuration file is useful when working on multi-modules project. Indeed you can set for each module exactly the template packs that should be
     * used. Keep in mind that only the template packs definition will be extracted from this file.
     * <p>
     * The default value is <div class="filename">src/main/config/celerio-maven-plugin/celerio-template-packs.xml</div>
     */
    @Parameter(property = "celerio-maven-plugin.packs.configuration", defaultValue = "${basedir}/src/main/config/celerio-maven-plugin/celerio-template-packs.xml")
    protected String xmlTemplatePacksOverride;

    /**
     * The relative path to the metadata.xml file produced by the dbmetadata-maven-plugin:extract-metadata goal.
     * <p>
     * If this file exists it will be used, otherwise Celerio will access the database directly.
     * <p>
     * The main purpose of this file is to speed-up the generation process, as for large database schema the reverse engineering takes time. An other very
     * important benefit of this feature is to store the file in your source control, thus having a reproducible build.
     */
    @Parameter(property = "celerio-maven-plugin.xml.metadata", defaultValue = "${basedir}/src/main/config/celerio-maven-plugin/metadata.xml")
    protected String xmlMetadata;

    /**
     * Should the source code generation be skipped ?
     * <p>
     * This is a common pattern in Maven, where you can skip plugins using profiles to fully adapt your build.
     */
    @Parameter(property = "celerio-maven-plugin.skip", defaultValue = "false")
    protected boolean skip;

    /**
     * Specify the JDBC driver.
     * <p>
     * Example: <code>org.postgresql.Driver</code>
     */
    @Parameter(property = "jdbc.driver")
    protected String jdbcDriver;

    /**
     * Specify the JDBC url.
     * <p>
     * Example: <code>jdbc:h2:~/.h2/sampledatabase</code>
     */
    @Parameter(property = "jdbc.url")
    protected String jdbcUrl;

    /**
     * Specify the JDBC user, this user needs to have the privilege to access the database metadata.
     */
    @Parameter(property = "jdbc.user")
    protected String jdbcUser;

    /**
     * Specify the JDBC password.
     */
    @Parameter(property = "jdbc.password")
    protected String jdbcPassword;

    /**
     * Specify the JDBC catalog.
     */
    @Parameter(property = "jdbc.catalog")
    protected String jdbcCatalog;

    /**
     * Should the Oracle remarks be retrieved ? Please note that this will impact the speed of the reverse engineering of your database.
     */
    @Parameter(property = "jdbc.oracleRetrieveRemarks", defaultValue = "false")
    protected boolean jdbcOracleRetrieveRemarks;

    /**
     * Should the synonyms be retrieved ?
     */
    @Parameter(property = "jdbc.oracleRetrieveSynonyms", defaultValue = "true")
    protected boolean jdbcOracleRetrieveSynonyms;

    /**
     * Specify the JDBC schema.
     */
    @Parameter(property = "jdbc.schema")
    protected String jdbcSchema;

    /**
     * Run celerio in 'springfuse' mode.
     */
    @Parameter(property = "springfuseMode", defaultValue = "false")
    protected boolean springfuseMode;

    public String getPluginPackage() {
        return "com.jaxio.celerio";
    }

    public String getPluginName() {
        return "celerio-maven-plugin";
    }

    public String getPluginGoal() {
        return "generate";
    }

    /*
     * There are few hacks that we must apply when running springfuse (for example, Module settings)
     */
    public boolean getSpringfuseMode() {
        return false;
    }

    public void execute() throws MojoExecutionException {
        try {
            process();
        } catch (Exception e) {
            getLog().error(e.getMessage());
            throw new MojoExecutionException(e.getLocalizedMessage(), e);
        }
    }

    private void process() throws Exception {
        if (skip) {
            getLog().info("skipping maven plugin");
            return;
        }

        context = new ClassPathXmlApplicationContext("classpath:applicationContext-celerio.xml");
        CelerioProducer producer = context.getBean(CelerioProducer.class);
        Config config = setupConfig(producer.getConfig(), xmlMetadata, xmlConfiguration, baseDir, outputDirectory);
        config.setSpringfuseMode(getSpringfuseMode());
        exitIfConfigurationMissing(config);
        exitIfNoPackDefined(producer.getPackLoader());
        producer.produce();
    }

    private void exitIfConfigurationMissing(Config config) throws MojoExecutionException {
        if (isNull(config.getMetadata(), config.getCelerio(), config.getOutputResult())) {
            String message = "";
            if (isNull(config.getMetadata())) {
                message += Metadata.class.getSimpleName() + " ";
            }
            if (isNull(config.getCelerio())) {
                message += Celerio.class.getSimpleName() + " ";
            }
            if (isNull(config.getOutputResult())) {
                message += OutputResult.class.getSimpleName() + " ";
            }

            throw new MojoExecutionException("Please configure the plugin and set " + message);
        }
    }

    private void exitIfNoPackDefined(PackLoader packLoader) throws MojoExecutionException {
        if (!packLoader.hasPack()) {
            throw new MojoExecutionException("No pack available, add pack dependencies or local celerio folder");
        }
    }

    private Config setupConfig(Config config, String xmlMetadata, String xmlConfiguration, String baseDir, String outputDirectory) throws MojoExecutionException,
        XmlMappingException, IOException, ClassNotFoundException, SQLException {
        config.setMetadata(getMetaData(xmlMetadata));
        config.setCelerio(getCelerio(xmlConfiguration));
        config.setOutputResult(getOutput(baseDir, outputDirectory));
        config.setBaseDir(baseDir);
        getLog().info("Using outputDir: " + outputDirectory);
        return config;
    }

    private OutputResult getOutput(String baseDir, String outputDirectory) {
        OutputResultFactory outputResultFactory = context.getBean(OutputResultFactory.class);
        return outputResultFactory.getOutputResult(baseDir, outputDirectory);
    }

    private Metadata getMetaData(String xmlMetaData) throws XmlMappingException, IOException, ClassNotFoundException, SQLException {
        if (isNotBlank(xmlMetaData)) {
            return getMetadataFromFile(xmlMetaData);
        } else if (FileUtils.fileExists(DEFAULT_XML_METADATA)) {
            return getMetadataFromFile(DEFAULT_XML_METADATA);
        } else {
            return getMetadataFromDatabase();
        }
    }

    private Metadata getMetadataFromDatabase() throws ClassNotFoundException, SQLException {
        JdbcConnectivity jdbcConnectivity = getDbMetaDataConfiguration();
        if (!jdbcConnectivity.invalid()) {
            displayMissingMetadataConfiguration(jdbcConnectivity);
            return null;
        }
        return extractMetaDataFromDatabase(jdbcConnectivity);
    }

    private Metadata extractMetaDataFromDatabase(JdbcConnectivity configuration) throws ClassNotFoundException, SQLException {
        MetadataExtractor extractor = context.getBean(MetadataExtractor.class);
        getLog().info("extracting metadata from database");
        return extractor.extract(configuration);
    }

    private JdbcConnectivity getDbMetaDataConfiguration() {
        JdbcConnectivity configuration = new JdbcConnectivity();
        configuration.setDriver(jdbcDriver);
        configuration.setUser(jdbcUser);
        configuration.setPassword(jdbcPassword);
        configuration.setUrl(jdbcUrl);
        configuration.setOracleRetrieveSynonyms(jdbcOracleRetrieveSynonyms);
        configuration.setOracleRetrieveRemarks(jdbcOracleRetrieveRemarks);
        configuration.add(TableType.TABLE);
        return configuration;
    }

    private void displayMissingMetadataConfiguration(JdbcConnectivity configuration) {
        if (StringUtils.isBlank(configuration.getDriver())) {
            getLog().error("jdbcDriver missing");
        }
        if (StringUtils.isBlank(configuration.getUser())) {
            getLog().error("jdbcUser missing");
        }
        if (StringUtils.isBlank(configuration.getUrl())) {
            getLog().error("jdbcUrl missing");
        }
    }

    private Metadata getMetadataFromFile(String filename) throws XmlMappingException, IOException {
        MetadataLoader metaDataLoader = context.getBean(MetadataLoader.class);
        return metaDataLoader.load(filename);
    }

    private Celerio getCelerio(String xmlConfiguration) {
        Celerio celerio;

        if (FileUtils.fileExists(xmlConfiguration)) {
            celerio = getCelerioConfigurationFromFile(xmlConfiguration);
        } else {
            getLog().warn("****** The Celerio configuration file [" + xmlConfiguration + "] could not be found. Will use default empty configuration ******");
            celerio = new Celerio();
        }

        overridePacksAndModules(celerio);
        return celerio;
    }

    /**
     * For convenience (multi module, springfuse, etc...) template packs and modules may be in a separate file... we process this file if it exists.
     *
     * @param celerio
     */
    private void overridePacksAndModules(Celerio celerio) {
        String filename = xmlTemplatePacksOverride;
        if (!FileUtils.fileExists(filename)) {
            return;
        }
        getLog().info("Overriding configuration with " + filename);

        CelerioLoader celerioLoader = context.getBean(CelerioLoader.class);
        try {
            Configuration configuration = celerioLoader.load(filename).getConfiguration();
            // override celerioContext
            CelerioTemplateContext ctc = configuration.getCelerioTemplateContext();
            if (ctc != null) {
                celerio.getConfiguration().setCelerioTemplateContext(ctc);
            }

            // override packs
            List<Pack> newPacks = configuration.getPacks();
            if (newPacks != null && !newPacks.isEmpty()) {
                celerio.getConfiguration().setPacks(newPacks);
            }

            // override modules
            List<com.jaxio.celerio.configuration.Module> newModules = configuration.getModules();
            if (newModules != null && !newModules.isEmpty()) {
                celerio.getConfiguration().setModules(newModules);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not do override: ["+ filename + "]", e);
        }
    }

    private Celerio getCelerioConfigurationFromFile(String filename) throws XmlMappingException {
        CelerioLoader celerioLoader = context.getBean(CelerioLoader.class);
        try {
            return celerioLoader.load(filename);
        } catch (Exception e) {
            throw new RuntimeException("Error while loading Celerio with conf "+ filename, e);
        }
    }


    private static <T> boolean isNull(T... ts) {
        for (T t : ts) {
            if (t == null) {
                return true;
            }
        }
        return false;
    }
}