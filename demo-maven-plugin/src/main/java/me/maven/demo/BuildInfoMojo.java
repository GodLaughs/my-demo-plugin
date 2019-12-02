package me.maven.demo;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import com.puppycrawl.tools.checkstyle.api.FileText;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @goal buildinfo
 * @phase pre-integration-test
 */
public class BuildInfoMojo extends AbstractMojo {

    /**
     * @parameter expression="${buildinfo.prefix}"
     * default-value="我的前缀"
     */
    private String prefix;

    /**
     * @parameter expression = "${project.basedir}"
     * @readonly
     * @required
     */
    private File baseDir;

    /**
     * @parameter expression = "${project.build.sourceDirectory}"
     * @readonly
     * @required
     */
    private File sourceDirectory;


    /**
     * @parameter expression = "${project.build.testSourceDirectory}"
     * @readonly
     * @required
     */
    private File testSourceDirectory;


    /**
     * @parameter expression = "${project.build.resources}"
     * @readonly
     * @required
     */
    private List<Resource> resources;

    /**
     * @parameter expression = "${project.build.testResources}"
     * @readonly
     * @required
     */
    private List<Resource> testResources;

    /**
     * The file types which will be incluede for counting
     *
     * @parameter
     */
    private String[] includes;

    private static final String[] INCLUDES_DEFAULT = {"java"};

    public void execute() throws MojoExecutionException {
        getLog().info("\n==========地址输出中================ \nProject build info:");
        String[] info = {sourceDirectory.getAbsolutePath(), testSourceDirectory.getAbsolutePath()};
        for (String item : info) {
            getLog().info("\t" + prefix + "   " + item);
        }
        getLog().info("\n===========地址输出结束============");


        getLog().info("\n===========代码行数计数中============");
        //如果没有在pom.xml中说明includes，就是用默认的includes。
        if (includes == null || includes.length == 0) {
            includes = INCLUDES_DEFAULT;
        }
        try {
            //分别统计四种目录下的代码行数。
            countDir(sourceDirectory);
            countDir(testSourceDirectory);
            for (Resource resource : resources) {
                countDir(new File(resource.getDirectory()));
            }
            for (Resource resource : testResources) {
                countDir(new File(resource.getDirectory()));
            }

            getLog().info("\n===========代码行数结束============");
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to count lines of code.", e);
        }

        getLog().info("\n===========检查字段驼峰中============");

        try {
            checkHump(sourceDirectory);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to check hump of code.", e);
        } catch (RecognitionException e) {
            e.printStackTrace();
        } catch (TokenStreamException e) {
            e.printStackTrace();
        }
        getLog().info("\n===========检查字段驼峰结束============");
    }


    /**
     * 统计该目录下所有文件的代码行数之和。
     *
     * @param dir
     * @throws IOException
     */
    private void countDir(File dir) throws IOException {
        if (!dir.exists()) {
            return;
        }
        List<File> collected = new ArrayList<File>();
        collectFiles(collected, dir);
        int lines = 0;
        for (File sourceFile : collected) {
            lines += countLine(sourceFile);
        }
        String path = dir.getAbsolutePath().substring(baseDir.getAbsolutePath().length());
        getLog().info(path + ":   " + lines + " lines of code in " + collected.size() + " files");
    }

    /**
     * 递归的统计该目录下的所有文件，并放入list
     *
     * @param collected
     * @param file
     */
    private void collectFiles(List<File> collected, File file) {
        if (file.isFile()) {
            for (String include : includes) {
                if (file.getName().endsWith("." + include)) {
                    collected.add(file);
                    break;
                }
            }
        } else {
            for (File sub : file.listFiles()) {
                collectFiles(collected, sub);
            }
        }
    }

    /**
     * 统计代码行数。
     *
     * @param file
     * @return
     * @throws IOException
     */
    private int countLine(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int line = 0;
        try {
            while (reader.ready()) {
                reader.readLine();
                line++;
            }
        } finally {
            reader.close();
        }
        return line;
    }

    /**
     * 检查驼峰
     *
     * @param dir
     * @return
     * @throws IOException
     */
    private void checkHump(File dir) throws IOException, RecognitionException, TokenStreamException {
        if (!dir.exists()) {
            return;
        }
        List<File> collected = new ArrayList<File>();
        collectFiles(collected, dir);
        for (File file : collected) {
            processFile(file);
        }
    }

    private void processFile(File file) throws IOException, TokenStreamException, RecognitionException {
        final FileText theText = new FileText(file.getAbsoluteFile(), "UTF-8");
        HumpCheckService humpCheck = new HumpCheckService();
        humpCheck.process(theText);
    }

}