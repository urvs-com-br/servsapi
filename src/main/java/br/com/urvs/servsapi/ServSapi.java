package br.com.urvs.servsapi;

import java.io.File;
import java.nio.file.Files;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import br.com.urvs.servsapi.data.Air;
import br.com.urvs.servsapi.data.Groups;
import br.com.urvs.servsapi.data.Setup;
import br.com.urvs.servsapi.data.Users;
import br.com.urvs.servsapi.data.Way;

public class ServSapi {

  public static void main(String[] args) throws Exception {
    var options = cmdOptions();
    var command = new DefaultParser().parse(options, args);
    if (command.hasOption('?')) {
      System.out.println(
          "ServSapi (Server for Sapience) is the worker behind the services of the website www.urvs.com.br");
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("servsapi", options);
      return;
    }
    Setup setup;
    var setupFile = new File("setup.json");
    if (setupFile.exists()) {
      setup = Setup.fromString(Files.readString(setupFile.toPath()));
    } else {
      setup = new Setup();
    }
    setFromCmd(command, setup);
    Users users;
    var usersFile = new File("users.json");
    if (usersFile.exists()) {
      users = Users.fromString(Files.readString(usersFile.toPath()));
    } else {
      users = new Users();
    }
    Groups groups;
    var groupsFile = new File("groups.json");
    if (groupsFile.exists()) {
      groups = Groups.fromString(Files.readString(groupsFile.toPath()));
    } else {
      groups = new Groups();
    }
    setup.fixDefaults();
    users.fixDefaults();
    new Service(new Way(new Air(setup, users, groups))).start();
  }

  public static Options cmdOptions() {
    var result = new Options();
    result.addOption(Option.builder("?").longOpt("help")
        .desc("Print usage information.").build());
    result.addOption(Option.builder("v").longOpt("verbose")
        .desc("Should we print verbose messages?").build());
    result.addOption(Option.builder("k").longOpt("archive")
        .desc("Should we archive all the messages?").build());
    result.addOption(Option.builder("n").longOpt("name").hasArg()
        .desc("On behalf of what name should we serve?").build());
    result.addOption(Option.builder("l").longOpt("lang").hasArg()
        .desc("On what language should we serve?").build());
    result.addOption(Option.builder("h").longOpt("host").hasArg()
        .desc("On what host should we serve?").build());
    result.addOption(Option.builder("p").longOpt("port").hasArg()
        .desc("On what port should we serve?").build());
    result.addOption(Option.builder("f").longOpt("folder").hasArg()
        .desc("On what folder should we serve?").build());
    result.addOption(Option.builder("u").longOpt("serves-pub")
        .desc("Should we serve public files?").build());
    result.addOption(Option.builder("d").longOpt("serves-dir")
        .desc("Should we serve directories?").build());
    return result;
  }

  public static void setFromCmd(CommandLine command, Setup setup) {
    if (command.hasOption('v')) {
      setup.serverVerbose = true;
    }
    if (command.hasOption('k')) {
      setup.serverArchive = true;
    }
    if (command.hasOption('n')) {
      setup.serverName = command.getOptionValue('n');
    }
    if (command.hasOption('l')) {
      setup.serverLang = command.getOptionValue('l');
    }
    if (command.hasOption('h')) {
      setup.serverHost = command.getOptionValue('h');
    }
    if (command.hasOption('p')) {
      setup.serverPort = Integer.parseInt(command.getOptionValue('p'));
    }
    if (command.hasOption('f')) {
      setup.serverFolder = command.getOptionValue('f');
    }
    if (command.hasOption('u')) {
      setup.servesPUB = true;
    }
    if (command.hasOption('d')) {
      setup.servesDIR = true;
    }
  }

}
