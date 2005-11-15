package scbaz;

import java.io.{File, StringReader} ;
import scala.xml._ ;


// Information about one package that is currently installed.
// The "complete" flag indicates whether installation
// is complete.
class InstalledEntry(val name:String, val version:Version,
		     val files:List[File],
		     val complete:Boolean)
{

// XXX the following causes a compiler crash
//  def this(name0:String, version0:Version, files0:List[File]) = {
//    this(name0, version0, files0, false);
//  }

  val packageSpec = PackageSpec(name, version) ;

  // return the same entry but with complete=true
  def completed = {
    new InstalledEntry(name, version, files, true)
  }

  def toXML:Node = {
    val base_elements = List(
	 Elem(null, "name", Null, TopScope,
	      Text(name)),
	 Elem(null, "version", Null, TopScope,
	      Text(version.toString())),
	 Elem(null, "files", Null, TopScope,
	      (files.map(f =>
		Elem(null, "filename", Null, TopScope,
		     Text(f.getPath())))) : _* )) ;

    val elements =
      if(complete)
	base_elements ::: List(Elem(null, "complete", Null, TopScope))
      else
	base_elements ;



    Elem(null, "installedpackage", Null, TopScope,
	 elements : _* )
  }

  override def toString() = {
    name + " " + version + 
    " (" + files.length + " files)" +
    (if(complete) "" else " (incomplete)")
  }
}



object InstalledEntry {
  def fromXML(xml:Node) = {
    // XXX need to throw a reasonable error for malformed input
    val parts = xml ;

    val name = (parts \ "name")(0).child(0).toString(false) ;
    val version = new Version((parts \ "version")(0).child(0).toString(false)) ;
    val files =
         (parts \ "files" \ "filename").toList.map(s =>
                 new File(s(0).child(0).toString(false))) ;
    val complete = (parts \ "complete").length > 0 ;

    new InstalledEntry(name, version, files, complete)
  }
}



object TestInstalledEntry {
  def main(args:Array[String]) = {
    val xml =
      "<installedpackage>\n" +
      "<name>foo</name>\n" +
      "<version>1.5</version>\n" +
      "<files>\n" +
      "  <filename>lib/foo.jar</filename>\n" +
      "  <filename>doc/foo/foo.html</filename>\n" +
      "</files>\n" +
      "<complete/>\n" +
      "</installedpackage>\n" ;

    val node = XML.load(new StringReader(xml)) ;
    val entry = InstalledEntry.fromXML(node) ;

    Console.println(entry);
    Console.println(entry.toXML);
  }
}
