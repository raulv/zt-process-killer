package org.zeroturnaround.process;

import java.util.Arrays;

import org.apache.commons.lang.SystemUtils;


/**
 * Creates {@link SystemProcess} instances.
 */
public class Processes {

  /**
   * Creates an instance that represents the given {@link Process} by detecting its PID
   * using both, {@link Process} object and external tools.
   * Java APIs are tried before using the external tools.
   */
  public static SystemProcess newStandardProcess(Process process) {
    return newStandardProcess(process, PidUtil.getPid(process));
  }

  /**
   * Creates an instance that represents the given {@link Process} or the given PID.
   * They are expected to belong to the same system process.
   * Java APIs are tried before using the external tools (PID value).
   */
  public static SystemProcess newStandardProcess(Process process, int pid) {
    return newProcessWithAtlernatives(newJavaProcess(process), newPidProcess(pid));
  }

  /**
   * Creates an instance that represents the given {@link Process} object.
   */
  public static JavaProcess newJavaProcess(Process process) {
    if (Java8Process.isSupported())
      return new Java8Process(process);
    return new JavaProcess(process);
  }

  /**
   * Creates an instance that represents the PID value of the given {@link Process}.
   * The instance uses external tools for killing the process.
   */
  public static PidProcess newPidProcess(Process process) {
    return newPidProcess(PidUtil.getPid(process));
  }

  /**
   * Creates an instance that represents the given PID value.
   * The instance uses external tools for killing the process.
   */
  public static PidProcess newPidProcess(int pid) {
    if (SystemUtils.IS_OS_WINDOWS) {
      return new WindowsProcess(pid);
    }
    if (SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS) {
      return new SolarisProcess(pid);
    }
    return new UnixProcess(pid);
  }

  /**
   * Combines existing {@link SystemProcess} objects as alternative implementations for a single process.
   * 
   * @deprecated Use {@link #newProcessWithAlternatives(SystemProcess...)} instead.
   */
  public static SystemProcess newProcessWithAtlernatives(SystemProcess... processes) {
    return newProcessWithAlternatives(processes);
  }

  /**
   * Combines existing {@link SystemProcess} objects as alternative implementations for a single process.
   */
  public static SystemProcess newProcessWithAlternatives(SystemProcess... processes) {
    return new OrProcess(Arrays.asList(processes));
  }

  /**
   * Combines existing {@link SystemProcess} objects for multiple processes.
   */
  public static SystemProcess newProcessForMultiple(SystemProcess... processes) {
    return new AndProcess(Arrays.asList(processes));
  }

}
